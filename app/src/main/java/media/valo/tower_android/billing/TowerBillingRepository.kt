/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.billing

//
//  TowerBillingRepository.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import media.valo.tower_android.BuildConfig
import media.valo.tower_android.data.local.preferences.subscription.SubscriptionRepository
import media.valo.tower_android.model.SubscriptionStatus
import media.valo.tower_android.utils.AppScope
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

private const val TAG = "TowerBillingRepository"

@Singleton
class TowerBillingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subscriptionRepository: SubscriptionRepository,
    @AppScope private val appScope: CoroutineScope
) : PurchasesUpdatedListener {

    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .enableAutoServiceReconnection()
        .build()

    private var selectedOffer: SelectedOffer? = null
    private val refreshMutex = Mutex()

    init {
        appScope.launch {
            val cachedSubscription = subscriptionRepository.getCachedSubscription()
            _uiState.update {
                it.copy(
                    status = cachedSubscription.status,
                    productId = cachedSubscription.productId ?: BuildConfig.TOWER_BILLING_PRODUCT_ID,
                    isUsingCachedEntitlement = cachedSubscription.status == SubscriptionStatus.ACTIVE
                )
            }
            refreshMutex.withLock {
                syncBillingState(clearError = true)
            }
        }
    }

    fun refresh() {
        appScope.launch {
            refreshMutex.withLock {
                syncBillingState(clearError = true)
            }
        }
    }

    fun restorePurchases() {
        appScope.launch {
            refreshMutex.withLock {
                syncBillingState(clearError = true)
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity) {
        appScope.launch {
            if (!uiState.value.isBillingAvailable) {
                refreshMutex.withLock {
                    syncBillingState(clearError = true)
                }
            }

            val offer = selectedOffer
            if (offer == null) {
                setError("Das Abo ist in Google Play noch nicht verfügbar.")
                return@launch
            }

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(offer.productDetails)
                            .setOfferToken(offer.offerToken)
                            .build()
                    )
                )
                .build()

            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
            if (billingResult.responseCode != BillingResponseCode.OK) {
                if (billingResult.responseCode != BillingResponseCode.USER_CANCELED) {
                    setError(billingResult.debugMessage.ifBlank {
                        "Der Kauf konnte nicht gestartet werden."
                    })
                } else {
                    clearError()
                }
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                appScope.launch {
                    processPurchases(purchases.orEmpty())
                }
            }

            BillingResponseCode.USER_CANCELED -> clearError()

            else -> setError(
                billingResult.debugMessage.ifBlank { "Google Play konnte den Kauf nicht abschließen." }
            )
        }
    }

    private suspend fun syncBillingState(clearError: Boolean) {
        if (clearError) {
            clearError()
        }
        _uiState.update {
            it.copy(
                isLoading = true,
                verificationStatus = EntitlementVerificationStatus.VERIFYING
            )
        }

        val isConnected = ensureConnected()
        if (!isConnected) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    verificationStatus = EntitlementVerificationStatus.FAILED,
                    isBillingAvailable = false
                )
            }
            setError("Google Play Billing ist derzeit nicht erreichbar.")
            return
        }

        val isSubscriptionsSupported = billingClient
            .isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
            .responseCode == BillingResponseCode.OK
        if (!isSubscriptionsSupported) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    verificationStatus = EntitlementVerificationStatus.FAILED,
                    isBillingAvailable = false,
                    isProductAvailable = false
                )
            }
            setError("Dieses Gerät unterstützt keine Google-Play-Abos.")
            return
        }

        val offer = queryLaunchOffer()
        selectedOffer = offer
        _uiState.update {
            it.copy(
                isBillingAvailable = true,
                isProductAvailable = offer != null,
                productId = offer?.productDetails?.productId ?: it.productId,
                planName = offer?.planName ?: it.planName,
                trialLabel = offer?.trialLabel ?: it.trialLabel,
                monthlyPriceLabel = offer?.monthlyPriceLabel ?: it.monthlyPriceLabel
            )
        }

        val purchases = queryPurchases()
        if (purchases == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    verificationStatus = EntitlementVerificationStatus.FAILED
                )
            }
            setError("Der Abo-Status konnte nicht aktualisiert werden.")
            return
        }

        processPurchases(purchases)
    }

    private suspend fun processPurchases(purchases: List<Purchase>) {
        val relevantPurchases = purchases.filter { purchase ->
            purchase.products.contains(uiState.value.productId)
        }

        val activePurchase = relevantPurchases.firstOrNull {
            it.purchaseState == Purchase.PurchaseState.PURCHASED
        }
        val pendingPurchase = relevantPurchases.firstOrNull {
            it.purchaseState == Purchase.PurchaseState.PENDING
        }

        when {
            activePurchase != null -> {
                if (!activePurchase.isAcknowledged) {
                    acknowledgePurchase(activePurchase)
                }
                subscriptionRepository.setCachedSubscription(
                    status = SubscriptionStatus.ACTIVE,
                    productId = activePurchase.products.firstOrNull(),
                    purchaseToken = activePurchase.purchaseToken
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        verificationStatus = EntitlementVerificationStatus.VERIFIED,
                        status = SubscriptionStatus.ACTIVE,
                        isUsingCachedEntitlement = false
                    )
                }
            }

            pendingPurchase != null -> {
                subscriptionRepository.setCachedSubscription(
                    status = SubscriptionStatus.PENDING,
                    productId = pendingPurchase.products.firstOrNull(),
                    purchaseToken = pendingPurchase.purchaseToken
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        verificationStatus = EntitlementVerificationStatus.VERIFIED,
                        status = SubscriptionStatus.PENDING,
                        isUsingCachedEntitlement = false
                    )
                }
            }

            else -> {
                subscriptionRepository.setCachedSubscription(
                    status = SubscriptionStatus.NONE,
                    productId = selectedOffer?.productDetails?.productId ?: uiState.value.productId,
                    purchaseToken = null
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        verificationStatus = EntitlementVerificationStatus.VERIFIED,
                        status = SubscriptionStatus.NONE,
                        isUsingCachedEntitlement = false
                    )
                }
            }
        }
    }

    private suspend fun acknowledgePurchase(purchase: Purchase) {
        val billingResult = suspendCancellableCoroutine<BillingResult> { continuation ->
            billingClient.acknowledgePurchase(
                AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
            ) { result ->
                if (continuation.isActive) {
                    continuation.resume(result)
                }
            }
        }

        if (billingResult.responseCode != BillingResponseCode.OK) {
            Log.w(
                TAG,
                "Acknowledge purchase failed: ${billingResult.responseCode} ${billingResult.debugMessage}"
            )
        }
    }

    private suspend fun queryLaunchOffer(): SelectedOffer? {
        val productId = BuildConfig.TOWER_BILLING_PRODUCT_ID
        if (productId.isBlank()) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(ProductType.SUBS)
                            .build()
                    )
                )
                .build()

            billingClient.queryProductDetailsAsync(queryProductDetailsParams) { result, details ->
                if (!continuation.isActive) {
                    return@queryProductDetailsAsync
                }

                if (result.responseCode != BillingResponseCode.OK) {
                    Log.w(
                        TAG,
                        "Product details query failed: ${result.responseCode} ${result.debugMessage}"
                    )
                    continuation.resume(null)
                    return@queryProductDetailsAsync
                }

                val productDetails = details.productDetailsList.firstOrNull()
                continuation.resume(productDetails?.let(::selectOffer))
            }
        }
    }

    private suspend fun queryPurchases(): List<Purchase>? =
        suspendCancellableCoroutine { continuation ->
            val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                .setProductType(ProductType.SUBS)
                .build()

            billingClient.queryPurchasesAsync(queryPurchasesParams) { result, purchases ->
                if (!continuation.isActive) {
                    return@queryPurchasesAsync
                }

                if (result.responseCode != BillingResponseCode.OK) {
                    Log.w(TAG, "Purchase query failed: ${result.responseCode} ${result.debugMessage}")
                    continuation.resume(null)
                    return@queryPurchasesAsync
                }

                continuation.resume(purchases)
            }
        }

    private suspend fun ensureConnected(): Boolean =
        suspendCancellableCoroutine { continuation ->
            if (billingClient.isReady) {
                continuation.resume(true)
                return@suspendCancellableCoroutine
            }

            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    continuation.resume(billingResult.responseCode == BillingResponseCode.OK)
                }

                override fun onBillingServiceDisconnected() {
                    if (continuation.isActive) {
                        continuation.resume(false)
                    }
                }
            })
        }

    private fun selectOffer(productDetails: ProductDetails): SelectedOffer? {
        val offerDetails = productDetails.subscriptionOfferDetails.orEmpty()
        val selectionConfig = OfferSelectionConfig(
            basePlanId = BuildConfig.TOWER_BILLING_BASE_PLAN_ID.blankToNull(),
            offerId = BuildConfig.TOWER_BILLING_OFFER_ID.blankToNull()
        )
        val offerSnapshot = selectOfferSnapshot(
            candidates = offerDetails.map { details ->
                OfferSelectionSnapshot(
                    offerToken = details.offerToken,
                    basePlanId = details.basePlanId,
                    offerId = details.offerId,
                    hasFreePhase = details.pricingPhases.pricingPhaseList.any {
                        it.priceAmountMicros == 0L
                    },
                    pricingPhaseSignature = details.pricingPhases.pricingPhaseList.joinToString(
                        separator = "|"
                    ) { phase ->
                        "${phase.billingPeriod}:${phase.priceAmountMicros}"
                    }
                )
            },
            config = selectionConfig
        )
        val offer = offerDetails.firstOrNull { it.offerToken == offerSnapshot?.offerToken }
            ?: return null

        if (offerDetails.size > 1) {
            when {
                selectionConfig.isConfigured ->
                    Log.i(
                        TAG,
                        "Selected configured subscription offer " +
                            "basePlanId=${offer.basePlanId}, offerId=${offer.offerId}"
                    )

                else ->
                    Log.w(
                        TAG,
                        "Multiple subscription offers are available for " +
                            "${productDetails.productId} but no base plan or offer id is configured. " +
                            "Using deterministic fallback selection."
                    )
            }
        }

        val pricingPhases = offer.pricingPhases.pricingPhaseList
        val trialPhase = pricingPhases.firstOrNull { it.priceAmountMicros == 0L }
        val paidPhase = pricingPhases.lastOrNull { it.priceAmountMicros > 0L }

        return SelectedOffer(
            productDetails = productDetails,
            offerToken = offer.offerToken,
            planName = productDetails.title.substringBefore("(").trim().ifBlank {
                "TOWER 26 Launch"
            },
            trialLabel = trialPhase?.billingPeriod?.let(::billingPeriodToLabel) ?: "26 Tage kostenlos",
            monthlyPriceLabel = paidPhase?.formattedPrice?.let { "$it / Monat" } ?: "26 € / Monat"
        )
    }

    private fun billingPeriodToLabel(period: String): String = when (period) {
        "P26D" -> "26 Tage kostenlos"
        "P1M" -> "1 Monat kostenlos"
        else -> "26 Tage kostenlos"
    }

    private fun setError(message: String) {
        _uiState.update { it.copy(errorMessage = message, isLoading = false) }
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

internal data class OfferSelectionConfig(
    val basePlanId: String? = null,
    val offerId: String? = null
) {
    val isConfigured: Boolean
        get() = basePlanId != null || offerId != null
}

internal data class OfferSelectionSnapshot(
    val offerToken: String,
    val basePlanId: String,
    val offerId: String?,
    val hasFreePhase: Boolean,
    val pricingPhaseSignature: String
)

internal fun selectOfferSnapshot(
    candidates: List<OfferSelectionSnapshot>,
    config: OfferSelectionConfig
): OfferSelectionSnapshot? {
    if (candidates.isEmpty()) {
        return null
    }

    if (config.isConfigured) {
        return candidates.firstOrNull { candidate ->
            (config.basePlanId == null || candidate.basePlanId == config.basePlanId) &&
                (config.offerId == null || candidate.offerId == config.offerId)
        }
    }

    return candidates.sortedWith(
        compareByDescending<OfferSelectionSnapshot> { it.hasFreePhase }
            .thenBy { it.basePlanId }
            .thenBy { it.offerId ?: "" }
            .thenBy { it.pricingPhaseSignature }
            .thenBy { it.offerToken }
    ).firstOrNull()
}

private data class SelectedOffer(
    val productDetails: ProductDetails,
    val offerToken: String,
    val planName: String,
    val trialLabel: String,
    val monthlyPriceLabel: String
)

private fun String.blankToNull(): String? = if (isBlank()) null else this
