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

import android.app.Application
import com.android.billingclient.api.TestPricingPhase
import com.android.billingclient.api.TestSubscriptionOffer
import com.android.billingclient.api.subscriptionProductDetails
import media.valo.tower_android.model.SubscriptionOffer
import media.valo.tower_android.model.SubscriptionTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//
//  PlayBillingDataSourceRobolectricTest.kt
//  Tower_Android
//

/**
 * The monthly base plan of a tier, as the Google Play Console is set up.
 */
private val monthlyBasePlan = TestSubscriptionOffer(
    basePlanId = "monthly",
    pricingPhases = listOf(TestPricingPhase(billingPeriod = "P1M", formattedPrice = "9,99 €"))
)

/**
 * Tests for how what Google Play answers with is turned into offers.
 *
 * The connection to Google Play cannot be tested here, so these pin down the mapping on its own,
 * fed with product details built the way the billing library builds them.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class PlayBillingDataSourceRobolectricTest {

    @Test
    fun `takes the name, description and monthly price from the store`() {
        val productDetails = subscriptionProductDetails(
            productId = SubscriptionTier.MINI.productId,
            name = "TOWER Mini",
            description = "Für den gelegentlichen Bedarf.",
            offers = listOf(monthlyBasePlan)
        )

        assertEquals(
            SubscriptionOffer(
                tier = SubscriptionTier.MINI,
                name = "TOWER Mini",
                description = "Für den gelegentlichen Bedarf.",
                formattedPrice = "9,99 €"
            ),
            productDetails.toSubscriptionOffer()
        )
    }

    @Test
    fun `maps every tier onto its product`() {
        for (tier in SubscriptionTier.entries) {
            val productDetails = subscriptionProductDetails(
                productId = tier.productId,
                offers = listOf(monthlyBasePlan)
            )

            assertEquals(tier, productDetails.toSubscriptionOffer()?.tier)
        }
    }

    @Test
    fun `prices by the base plan rather than an introductory offer`() {
        val introductoryOffer = TestSubscriptionOffer(
            basePlanId = "monthly",
            offerId = "first-month-free",
            pricingPhases = listOf(
                TestPricingPhase(billingPeriod = "P1M", formattedPrice = "0,00 €"),
                TestPricingPhase(billingPeriod = "P1M", formattedPrice = "4,99 €")
            )
        )
        val productDetails = subscriptionProductDetails(
            productId = SubscriptionTier.MINI.productId,
            offers = listOf(introductoryOffer, monthlyBasePlan)
        )

        assertEquals("9,99 €", productDetails.toSubscriptionOffer()?.formattedPrice)
    }

    @Test
    fun `prices by the monthly base plan when there are others`() {
        val yearlyBasePlan = TestSubscriptionOffer(
            basePlanId = "yearly",
            pricingPhases = listOf(
                TestPricingPhase(billingPeriod = "P1Y", formattedPrice = "99,99 €")
            )
        )
        val productDetails = subscriptionProductDetails(
            productId = SubscriptionTier.MINI.productId,
            offers = listOf(yearlyBasePlan, monthlyBasePlan)
        )

        assertEquals("9,99 €", productDetails.toSubscriptionOffer()?.formattedPrice)
    }

    @Test
    fun `prices by what the base plan costs once any earlier phases are over`() {
        val basePlanWithTrialPhase = TestSubscriptionOffer(
            basePlanId = "monthly",
            pricingPhases = listOf(
                TestPricingPhase(billingPeriod = "P1W", formattedPrice = "0,00 €"),
                TestPricingPhase(billingPeriod = "P1M", formattedPrice = "9,99 €")
            )
        )
        val productDetails = subscriptionProductDetails(
            productId = SubscriptionTier.MINI.productId,
            offers = listOf(basePlanWithTrialPhase)
        )

        assertEquals("9,99 €", productDetails.toSubscriptionOffer()?.formattedPrice)
    }

    @Test
    fun `makes no offer of a product without a monthly base plan`() {
        val yearlyBasePlan = TestSubscriptionOffer(
            basePlanId = "yearly",
            pricingPhases = listOf(
                TestPricingPhase(billingPeriod = "P1Y", formattedPrice = "99,99 €")
            )
        )
        val productDetails = subscriptionProductDetails(
            productId = SubscriptionTier.MINI.productId,
            offers = listOf(yearlyBasePlan)
        )

        assertNull(productDetails.toSubscriptionOffer())
    }

    @Test
    fun `makes no offer of a product without any base plan`() {
        val productDetails = subscriptionProductDetails(
            productId = SubscriptionTier.MINI.productId,
            offers = emptyList()
        )

        assertNull(productDetails.toSubscriptionOffer())
    }

    @Test
    fun `makes no offer of a product that belongs to no tier`() {
        val productDetails = subscriptionProductDetails(
            productId = "media.valo.tower_android.ultra",
            offers = listOf(monthlyBasePlan)
        )

        assertNull(productDetails.toSubscriptionOffer())
    }

}
