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

package com.android.billingclient.api

import org.json.JSONArray
import org.json.JSONObject

//
//  ProductDetailsTestFactory.kt
//  Tower_Android
//

/**
 * A pricing phase of a subscription offer, as the Play Store would describe it.
 *
 * @property billingPeriod  How often the phase bills, as an ISO 8601 duration such as `P1M`.
 * @property formattedPrice The price of the phase, formatted for the user.
 */
data class TestPricingPhase(
    val billingPeriod: String,
    val formattedPrice: String
)

/**
 * A subscription offer, as the Play Store would describe it.
 *
 * @property basePlanId     The id of the base plan the offer belongs to.
 * @property offerId        The id of the offer, or `null` for the base plan itself.
 * @property pricingPhases  What the offer costs over time, earliest phase first.
 */
data class TestSubscriptionOffer(
    val basePlanId: String,
    val offerId: String? = null,
    val pricingPhases: List<TestPricingPhase>
)

/**
 * Build the `ProductDetails` the Play Store would answer with for a subscription.
 *
 * The billing library only builds `ProductDetails` from the JSON the Play Store answers with,
 * through a constructor it keeps to its own package, which is why this lives in that package. The
 * keys are the ones the library reads, so a library update that renames them fails these tests
 * rather than the app.
 *
 * @param productId     The id of the product in the Google Play Console.
 * @param name          The display name of the product.
 * @param description   The description of the product.
 * @param offers        The offers of the product, base plans and introductory offers alike.
 * @return              The product details as the library would hand them out.
 */
fun subscriptionProductDetails(
    productId: String,
    name: String = "Name of $productId",
    description: String = "Description of $productId",
    offers: List<TestSubscriptionOffer>
): ProductDetails {
    val json = JSONObject()
        .put("productId", productId)
        .put("type", BillingClient.ProductType.SUBS)
        .put("title", "$name (Tower)")
        .put("name", name)
        .put("description", description)
        .put("subscriptionOfferDetails", JSONArray(offers.map { it.toJson() }))
    return ProductDetails(json.toString())
}

private fun TestSubscriptionOffer.toJson(): JSONObject = JSONObject()
    .put("basePlanId", basePlanId)
    // The library reads a missing offer id as `null`, which is how it marks a base plan.
    .apply { if (offerId != null) put("offerId", offerId) }
    .put("offerIdToken", "token-$basePlanId-${offerId ?: "base"}")
    .put("pricingPhases", JSONArray(pricingPhases.map { it.toJson() }))

private fun TestPricingPhase.toJson(): JSONObject = JSONObject()
    .put("billingPeriod", billingPeriod)
    .put("formattedPrice", formattedPrice)
    .put("priceAmountMicros", 0L)
    .put("priceCurrencyCode", "EUR")
    .put("recurrenceMode", 1)
    .put("billingCycleCount", 0)
