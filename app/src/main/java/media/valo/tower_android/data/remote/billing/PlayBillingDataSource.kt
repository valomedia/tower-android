/******************************************************************************
 * Copyright (c) 2026 valo.media GmbH                                         *
 * All rights reserved.                                                       *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU Affero General Public License as             *
 * published by the Free Software Foundation, either version 3 of the         *
 * License, or (at your option) any later version.                            *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Affero General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Affero General Public License   *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package media.valo.tower_android.data.remote.billing

//
//  PlayBillingDataSource.kt
//  Tower_Android
//

import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import media.valo.tower_android.model.SubscriptionOffer
import media.valo.tower_android.model.SubscriptionTier
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * Tag added to log messages related to the PlayBillingDataSource.
 */
private const val TAG = "PlayBillingDataSource"

/**
 * How long to give Google Play to connect and answer, in milliseconds.
 *
 * The billing library promises a callback for every request, but has been known to go quiet when
 * the Play Store is updating or missing. Rather than leave the pricing screen loading forever, the
 * query is given up on after this long and the tiers are shown without prices.
 */
private const val QUERY_TIMEOUT_MILLIS = 15_000L

/**
 * The billing period of a base plan that renews every month, as Google Play reports it.
 *
 * Google Play reports billing periods as ISO 8601 durations, `P1M` being one month.
 */
private const val MONTHLY_BILLING_PERIOD = "P1M"

/**
 * A `BillingDataSource` backed by the Google Play Billing Library.
 *
 * Every query opens its own connection to Google Play and closes it again once the answer is in.
 * The pricing screen is the only thing asking for now, and it asks once, so holding a connection
 * open across the whole app would just be one more thing to keep alive.
 *
 * @param context   `Context` dependency.
 */
class PlayBillingDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : BillingDataSource {

    private val subscriptionQuery: QueryProductDetailsParams = QueryProductDetailsParams
        .newBuilder()
        .setProductList(
            SubscriptionTier.entries.map { tier ->
                QueryProductDetailsParams.Product
                    .newBuilder()
                    .setProductId(tier.productId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            }
        )
        .build()

    override suspend fun getSubscriptionOffers(): List<SubscriptionOffer> {
        val billingClient = BillingClient
            .newBuilder(context)
            // The library refuses to build a client without a purchases listener, even though
            // nothing can be purchased yet. Google Play can still report purchases made elsewhere
            // (by a license tester through the Play Store, say) while the client is connected, and
            // that is worth a line in the log when a price that should be there is not.
            .setListener { billingResult, purchases ->
                Log.w(
                    TAG,
                    "Ignoring a purchase update (${billingResult.describe()}, "
                            + "${purchases?.size ?: 0} purchases): purchasing is not implemented"
                )
            }
            // The library also insists on pending purchases being enabled for one-time products,
            // although TOWER sells none.
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            )
            .build()

        // Only the round trip to Google Play is covered by the broad catch below. The mapping
        // after it is our own code, where an exception is a bug and should surface as one rather
        // than as a missing price.
        val productDetailsList = try {
            withTimeout(QUERY_TIMEOUT_MILLIS) {
                billingClient.connect()
                billingClient.querySubscriptions()
            }
        } catch (e: TimeoutCancellationException) {
            throw BillingException(
                "Google Play Billing did not answer within $QUERY_TIMEOUT_MILLIS ms",
                e
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: BillingException) {
            throw e
        } catch (e: Exception) {
            // The library throws on its own account now and then, `startConnection` for one has
            // been seen failing with an `IllegalStateException` when the Play Store would not
            // bind. None of that is worth more than a missing price, so it is reported the same
            // way as the store saying no.
            throw BillingException("Google Play Billing failed", e)
        } finally {
            billingClient.endConnection()
        }

        return productDetailsList.mapNotNull { productDetails ->
            productDetails.toSubscriptionOffer()
        }
    }

    private suspend fun BillingClient.connect() {
        val billingResult = suspendCancellableCoroutine { continuation ->
            startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (continuation.isActive) {
                        continuation.resume(billingResult)
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // The connection only lives for a single query and is never reconnected. A
                    // query that was underway gets no answer then and runs into the timeout, so
                    // this is logged to tell the two apart.
                    Log.w(TAG, "Google Play Billing disconnected")
                }
            })
        }
        billingResult.requireOk("Google Play Billing could not be reached")
    }

    /**
     * Ask Google Play for the subscriptions.
     *
     * This is not `queryProductDetails()` from `billing-ktx`, because that one hands back only the
     * product details and drops the list of products Google Play did not return, which
     * `logUnfetchedProducts()` needs.
     */
    private suspend fun BillingClient.querySubscriptions(): List<ProductDetails> {
        val (billingResult, queryResult) = suspendCancellableCoroutine { continuation ->
            queryProductDetailsAsync(subscriptionQuery) { billingResult, queryResult ->
                if (continuation.isActive) {
                    continuation.resume(billingResult to queryResult)
                }
            }
        }
        billingResult.requireOk("Google Play Billing refused to list the subscriptions")
        queryResult.logUnfetchedProducts()
        return queryResult.productDetailsList
    }

    private fun BillingResult.requireOk(message: String) {
        if (responseCode != BillingClient.BillingResponseCode.OK) {
            throw BillingException("$message: ${describe()}")
        }
    }

    /**
     * Log the products Google Play did not return.
     *
     * Products that are not published, not available in the user's country, or set up under
     * another id land here rather than in an error, so without this line a tier shown without a
     * price would leave no trace of why.
     */
    private fun QueryProductDetailsResult.logUnfetchedProducts() {
        for (unfetchedProduct in unfetchedProductList) {
            Log.w(
                TAG,
                "Google Play did not return ${unfetchedProduct.productId}: "
                        + "status ${unfetchedProduct.statusCode}"
            )
        }
    }

}

/**
 * Describe the outcome of a request in a way that can be read without the library's source.
 *
 * The response code is given by the name of its `BillingResponseCode` constant, as the number on
 * its own means nothing to whoever reads the log. The debug message is appended when there is one,
 * which there often is not.
 */
private fun BillingResult.describe(): String {
    val responseCodeName = when (responseCode) {
        BillingClient.BillingResponseCode.OK -> "OK"
        BillingClient.BillingResponseCode.USER_CANCELED -> "USER_CANCELED"
        BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> "SERVICE_UNAVAILABLE"
        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> "BILLING_UNAVAILABLE"
        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> "ITEM_UNAVAILABLE"
        BillingClient.BillingResponseCode.DEVELOPER_ERROR -> "DEVELOPER_ERROR"
        BillingClient.BillingResponseCode.ERROR -> "ERROR"
        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> "ITEM_ALREADY_OWNED"
        BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> "ITEM_NOT_OWNED"
        BillingClient.BillingResponseCode.NETWORK_ERROR -> "NETWORK_ERROR"
        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> "SERVICE_DISCONNECTED"
        BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> "FEATURE_NOT_SUPPORTED"
        else -> "unknown response code"
    }
    val outcome = "$responseCodeName ($responseCode)"
    return if (debugMessage.isBlank()) outcome else "$outcome: $debugMessage"
}

/**
 * Turn the product details Google Play returned into an offer for a tier.
 *
 * The price is taken from the monthly base plan. Base plans are the offers without an offer id,
 * and the one that renews every month is the one whose price the pricing screen labels as
 * monthly. A base plan on its own carries a single pricing phase, as introductory pricing lives
 * on the offers that are skipped here. The last phase is taken all the same, because that is what
 * the subscription costs once any earlier phases are over, should the library ever list some.
 *
 * A product that cannot be turned into an offer is logged, because that points at the product
 * being set up wrong in the Google Play Console.
 *
 * This is internal rather than private so that the mapping can be tested on its own, as the
 * connection to Google Play cannot be.
 *
 * @return The offer, or `null` if the product belongs to no tier or carries no monthly base plan.
 */
internal fun ProductDetails.toSubscriptionOffer(): SubscriptionOffer? {
    val tier = SubscriptionTier.fromProductId(productId)
    if (tier == null) {
        Log.w(TAG, "Google Play returned $productId, which belongs to no tier")
        return null
    }
    val price = subscriptionOfferDetails
        ?.filter { offerDetails -> offerDetails.offerId == null }
        ?.mapNotNull { basePlan -> basePlan.pricingPhases.pricingPhaseList.lastOrNull() }
        ?.firstOrNull { pricingPhase -> pricingPhase.billingPeriod == MONTHLY_BILLING_PERIOD }
    if (price == null) {
        Log.w(TAG, "Google Play returned $productId without a monthly base plan")
        return null
    }

    return SubscriptionOffer(
        tier = tier,
        name = name,
        description = description,
        formattedPrice = price.formattedPrice
    )
}
