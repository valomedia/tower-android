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

package media.valo.tower_android.ui.elements

import android.app.Application
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.runBlocking
import media.valo.tower_android.FakeCredentialDataSource
import media.valo.tower_android.FakeProfileDataSource
import media.valo.tower_android.FakeSettingsDataSource
import media.valo.tower_android.data.local.preferences.PreferencesManager
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import media.valo.tower_android.ui.routes.home.HomeScreen
import media.valo.tower_android.ui.routes.pricing.PricingScreen
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//
//  MenuRobolectricTest.kt
//  Tower_Android
//

/**
 * When the trial behind the menu started, in epoch milliseconds.
 */
private const val TRIAL_STARTED_AT = 1_700_000_000_000L

/**
 * Tests for what the menu says about the user's subscription, and where it leads.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class MenuRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * The settings behind the menu, with no call made yet.
     */
    private val settingsRepository = SettingsRepository(FakeSettingsDataSource())

    private lateinit var navController: NavHostController

    /**
     * Show the menu against the settings, next to a graph of empty destinations for it to
     * navigate.
     */
    private fun showMenu() {
        val viewModel = MenuViewModel(
            preferencesManager = PreferencesManager(
                settingsRepository = settingsRepository,
                credentialRepository = CredentialRepository(FakeCredentialDataSource()),
                profileRepository = ProfileRepository(FakeProfileDataSource())
            )
        )
        composeTestRule.setContent {
            navController = rememberNavController()

            NavHost(navController = navController, startDestination = HomeScreen) {
                composable<HomeScreen> { }
                composable<PricingScreen> { }
            }
            Menu(
                viewModel = viewModel,
                navController = navController,
                drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
            )
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `offers the subscription without a status before the first call`() {
        showMenu()

        composeTestRule.onNodeWithText("Abo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Testphase").assertDoesNotExist()
    }

    @Test
    fun `shows the user on the trial once their first call has started it`() {
        runBlocking { settingsRepository.startTrialIfNeeded(now = TRIAL_STARTED_AT) }

        showMenu()

        composeTestRule.onNodeWithText("Abo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Testphase").assertIsDisplayed()
    }

    @Test
    fun `opens the pricing when the subscription is tapped`() {
        showMenu()

        composeTestRule.onNodeWithText("Abo").performClick()
        composeTestRule.waitForIdle()

        assertTrue(navController.currentDestination?.hasRoute<PricingScreen>() == true)
        // Only the first call gets to announce the trial, not the menu.
        val route = navController.currentBackStackEntry?.toRoute<PricingScreen>()
        assertFalse(route?.isTrialJustStarted == true)
    }

}
