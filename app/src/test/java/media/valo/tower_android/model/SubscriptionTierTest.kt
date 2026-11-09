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

package media.valo.tower_android.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

//
//  SubscriptionTierTest.kt
//  Tower_Android
//

/**
 * Tests for how the tiers map onto the products in the Google Play Console.
 */
class SubscriptionTierTest {

    @Test
    fun `sells every tier under its product id in the Google Play Console`() {
        assertEquals("media.valo.tower_android.mini", SubscriptionTier.MINI.productId)
        assertEquals("media.valo.tower_android.basic", SubscriptionTier.BASIC.productId)
        assertEquals("media.valo.tower_android.power", SubscriptionTier.POWER.productId)
    }

    @Test
    fun `finds the tier behind a product id`() {
        for (tier in SubscriptionTier.entries) {
            assertEquals(tier, SubscriptionTier.fromProductId(tier.productId))
        }
    }

    @Test
    fun `finds no tier behind a product id that is not one of ours`() {
        assertNull(SubscriptionTier.fromProductId("media.valo.tower_android.ultra"))
    }

    @Test
    fun `lists the tiers smallest first`() {
        assertEquals(
            listOf(20, 50, 100),
            SubscriptionTier.entries.map { it.minutesPerMonth }
        )
    }

}
