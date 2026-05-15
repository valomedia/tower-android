/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.billing

//
//  SubscriptionUiState.kt
//  Tower_Android
//
//  Created by:
//      * Arne Engelland
//

import media.valo.tower_android.BuildConfig
import media.valo.tower_android.model.SubscriptionStatus

enum class EntitlementVerificationStatus {
    VERIFYING,
    VERIFIED,
    FAILED
}

private const val DEFAULT_PLAN_NAME = "TOWER 26 Launch"
private const val DEFAULT_TRIAL_LABEL = "26 Tage kostenlos"
private const val DEFAULT_PRICE_LABEL = "26 € / Monat"

/**
 * Ui state for the subscription paywall and status surfaces.
 */
data class SubscriptionUiState(
    val isLoading: Boolean = true,
    val status: SubscriptionStatus = SubscriptionStatus.NONE,
    val verificationStatus: EntitlementVerificationStatus = EntitlementVerificationStatus.VERIFYING,
    val isBillingAvailable: Boolean = false,
    val isProductAvailable: Boolean = false,
    val isUsingCachedEntitlement: Boolean = false,
    val planName: String = DEFAULT_PLAN_NAME,
    val trialLabel: String = DEFAULT_TRIAL_LABEL,
    val monthlyPriceLabel: String = DEFAULT_PRICE_LABEL,
    val minutesLabel: String = "100 Minuten pro Monat",
    val billingPeriodLabel: String = "Monatlich kündbar",
    val launchOfferLabel: String = "Launch-Angebot: Preis gilt nur in den ersten 3 Monaten.",
    val pricingDisclaimer: String = "Die finale Preisgestaltung steht noch nicht fest.",
    val productId: String = BuildConfig.TOWER_BILLING_PRODUCT_ID,
    val errorMessage: String? = null
) {
    val isEntitled: Boolean
        get() = status == SubscriptionStatus.ACTIVE && !isUsingCachedEntitlement

    val hasUnverifiedCachedEntitlement: Boolean
        get() = status == SubscriptionStatus.ACTIVE && isUsingCachedEntitlement

    val canPurchase: Boolean
        get() = !isEntitled
            && !hasUnverifiedCachedEntitlement
            && status != SubscriptionStatus.PENDING
            && isProductAvailable
            && !isLoading
}
