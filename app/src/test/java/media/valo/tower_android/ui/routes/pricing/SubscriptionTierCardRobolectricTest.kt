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

package media.valo.tower_android.ui.routes.pricing

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import media.valo.tower_android.model.SubscriptionOffer
import media.valo.tower_android.model.SubscriptionTier
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//
//  SubscriptionTierCardRobolectricTest.kt
//  Tower_Android
//

/**
 * Tests for what a tier's card says about the price in each state the store can be in.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class SubscriptionTierCardRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun showCard(offer: SubscriptionOffer?, isLoading: Boolean) {
        composeTestRule.setContent {
            SubscriptionTierCard(
                tier = SubscriptionTier.BASIC,
                offer = offer,
                isLoading = isLoading
            )
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `says the price is loading while the store is being asked`() {
        showCard(offer = null, isLoading = true)

        composeTestRule.onNodeWithText("Basic").assertIsDisplayed()
        composeTestRule.onNodeWithText("Preis wird geladen…").assertIsDisplayed()
    }

    @Test
    fun `says the price is unavailable once the store has not come through`() {
        showCard(offer = null, isLoading = false)

        composeTestRule.onNodeWithText("Basic").assertIsDisplayed()
        composeTestRule.onNodeWithText("Preis derzeit nicht verfügbar").assertIsDisplayed()
    }

    @Test
    fun `shows what the store has to say once it has answered`() {
        showCard(
            offer = SubscriptionOffer(
                tier = SubscriptionTier.BASIC,
                name = "TOWER Basic",
                description = "Für den regelmäßigen Bedarf.",
                formattedPrice = "19,99 €"
            ),
            isLoading = false
        )

        composeTestRule.onNodeWithText("TOWER Basic").assertIsDisplayed()
        composeTestRule.onNodeWithText("Für den regelmäßigen Bedarf.").assertIsDisplayed()
        composeTestRule.onNodeWithText("19,99 € pro Monat").assertIsDisplayed()
    }

}
