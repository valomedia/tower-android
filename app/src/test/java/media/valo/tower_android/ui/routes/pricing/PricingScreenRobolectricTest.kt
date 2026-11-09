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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import media.valo.tower_android.FakeBillingDataSource
import media.valo.tower_android.FakeSettingsDataSource
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import media.valo.tower_android.data.remote.billing.BillingRepository
import media.valo.tower_android.model.SubscriptionOffer
import media.valo.tower_android.model.SubscriptionTier
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//
//  PricingScreenRobolectricTest.kt
//  Tower_Android
//

/**
 * When the trial behind the screen started, in epoch milliseconds.
 */
private const val TRIAL_STARTED_AT = 1_700_000_000_000L

/**
 * The offers the store answers with when it can be reached.
 */
private val offers = listOf(
    SubscriptionOffer(
        tier = SubscriptionTier.MINI,
        name = "TOWER Mini",
        description = "Für den gelegentlichen Bedarf.",
        formattedPrice = "9,99 €"
    ),
    SubscriptionOffer(
        tier = SubscriptionTier.BASIC,
        name = "TOWER Basic",
        description = "Für den regelmäßigen Bedarf.",
        formattedPrice = "19,99 €"
    ),
    SubscriptionOffer(
        tier = SubscriptionTier.POWER,
        name = "TOWER Power",
        description = "Für den täglichen Bedarf.",
        formattedPrice = "34,99 €"
    )
)

/**
 * Tests for what the pricing screen puts on the screen.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class PricingScreenRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * The settings behind the screen, with the trial started and the notice still owed.
     */
    private val settingsRepository = SettingsRepository(FakeSettingsDataSource()).apply {
        runBlocking { startTrialIfNeeded(now = TRIAL_STARTED_AT) }
    }

    /**
     * Stands in for the app scope.
     *
     * Unconfined, so that what the screen hands off to it has run by the time the screen is idle,
     * as nothing behind the fakes ever suspends.
     */
    private val appScope = CoroutineScope(Dispatchers.Unconfined)

    /**
     * Show the pricing screen against a store in a given state.
     *
     * @param offers                The offers the store answers with, or `null` for an unreachable
     *                              store.
     * @param isTrialJustStarted    Whether the screen is shown right after the first call.
     */
    private fun showPricing(
        offers: List<SubscriptionOffer>?,
        isTrialJustStarted: Boolean = false
    ) {
        val viewModel = PricingViewModel(
            billingRepository = BillingRepository(FakeBillingDataSource(offers)),
            settingsRepository = settingsRepository,
            appScope = appScope
        )
        composeTestRule.setContent {
            PricingScreen(
                isTrialJustStarted = isTrialJustStarted,
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel
            )
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `shows every tier with its name and price from the store`() {
        showPricing(offers)

        for (offer in offers) {
            composeTestRule.onNodeWithText(offer.name).performScrollTo().assertIsDisplayed()
            composeTestRule
                .onNodeWithText("${offer.formattedPrice} pro Monat")
                .performScrollTo()
                .assertIsDisplayed()
        }
    }

    @Test
    fun `shows the minutes of every tier`() {
        showPricing(offers)

        for (tier in SubscriptionTier.entries) {
            composeTestRule
                .onNodeWithText("${tier.minutesPerMonth} Minuten pro Monat")
                .performScrollTo()
                .assertIsDisplayed()
        }
    }

    @Test
    fun `falls back to the tier names when the store cannot be reached`() {
        showPricing(offers = null)

        for (tier in SubscriptionTier.entries) {
            composeTestRule.onNodeWithText(tier.title).performScrollTo().assertIsDisplayed()
        }
        composeTestRule
            .onAllNodesWithText("Preis derzeit nicht verfügbar")
            .assertCountEquals(SubscriptionTier.entries.size)
    }

    @Test
    fun `falls back per tier when the store is missing some`() {
        val missingTier = SubscriptionTier.POWER
        showPricing(offers.filter { offer -> offer.tier != missingTier })

        for (offer in offers.filter { offer -> offer.tier != missingTier }) {
            composeTestRule
                .onNodeWithText("${offer.formattedPrice} pro Monat")
                .performScrollTo()
                .assertIsDisplayed()
        }
        composeTestRule.onNodeWithText(missingTier.title).performScrollTo().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Preis derzeit nicht verfügbar").assertCountEquals(1)
    }

    @Test
    fun `shows the free minutes and the success guarantee`() {
        showPricing(offers)

        composeTestRule
            .onNodeWithText("5 Minuten Fernassistenz geschenkt", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
        // The guarantee is worded by the business, so it is checked word for word.
        composeTestRule
            .onNodeWithText(
                "Erfolgsgarantie: wenn wir gemeinsam nicht erfolgreich waren, werden keine "
                        + "Minuten verbraucht."
            )
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun `tells the user about the trial only right after the first call`() {
        showPricing(offers, isTrialJustStarted = true)

        composeTestRule
            .onNodeWithText("Deine kostenlose Testphase hat begonnen und läuft 7 Tage.")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun `does not mention the trial when opened from the menu`() {
        showPricing(offers)

        composeTestRule.onNodeWithText("Testphase hat begonnen", substring = true).assertDoesNotExist()
    }

    @Test
    fun `records the trial notice as seen once it has been shown`() = runBlocking {
        showPricing(offers, isTrialJustStarted = true)

        assertFalse(settingsRepository.isTrialNoticePending())
    }

    @Test
    fun `leaves the trial notice owed when opened from the menu`() = runBlocking {
        showPricing(offers)

        assertTrue(settingsRepository.isTrialNoticePending())
    }

}
