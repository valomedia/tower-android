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

package media.valo.tower_android.ui.routes.call

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import media.valo.tower_android.ui.routes.home.HomeScreen
import media.valo.tower_android.ui.routes.pricing.PricingScreen
import media.valo.tower_android.ui.routes.pricing.navigateToPricing
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//
//  PostCallNavigationRobolectricTest.kt
//  Tower_Android
//

/**
 * Tests for what the end of a call leaves on the back stack.
 *
 * These run the real navigation against the real route objects on a graph of empty destinations,
 * so that no screen's dependencies have to be stood up to check where back leads.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class PostCallNavigationRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController

    @Before
    fun setUpNavGraph() {
        composeTestRule.setContent {
            navController = rememberNavController()

            NavHost(navController = navController, startDestination = HomeScreen) {
                composable<HomeScreen> { }
                composable<CallScreen> { }
                composable<PricingScreen> { }
            }
        }
        composeTestRule.runOnUiThread { navController.navigate(route = CallScreen) }
        composeTestRule.waitForIdle()
    }

    /**
     * Run the navigation the call screen would run once the call has ended.
     *
     * @param isTrialNoticePending  Whether the user has yet to be told about the trial.
     * @param isActivityFinishing   Whether the activity is already on its way out.
     */
    private fun endCall(isTrialNoticePending: Boolean, isActivityFinishing: Boolean = false) {
        composeTestRule.runOnUiThread {
            navController.navigateAfterCall(
                isTrialNoticePending = isTrialNoticePending,
                isActivityFinishing = isActivityFinishing
            )
        }
        composeTestRule.waitForIdle()
    }

    /**
     * Press back.
     */
    private fun goBack() {
        composeTestRule.runOnUiThread { navController.popBackStack() }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `goes home when the trial notice is not owed`() {
        endCall(isTrialNoticePending = false)

        assertTrue(navController.currentDestination?.hasRoute<HomeScreen>() == true)
    }

    @Test
    fun `shows the pricing over the home screen when the first call started the trial`() {
        endCall(isTrialNoticePending = true)
        assertTrue(navController.currentDestination?.hasRoute<PricingScreen>() == true)

        goBack()

        assertTrue(navController.currentDestination?.hasRoute<HomeScreen>() == true)
    }

    @Test
    fun `tells the pricing screen that the trial has just started`() {
        endCall(isTrialNoticePending = true)

        // Without this the pricing screen would never record the notice as seen, and startup
        // would keep bringing it back.
        val route = navController.currentBackStackEntry?.toRoute<PricingScreen>()
        assertTrue(route?.isTrialJustStarted == true)
    }

    @Test
    fun `leaves the trial notice for the next start when the call ends out of sight`() {
        endCall(isTrialNoticePending = true, isActivityFinishing = true)

        assertTrue(navController.currentDestination?.hasRoute<HomeScreen>() == true)
    }

    @Test
    fun `does not leave the call screen on the back stack`() {
        endCall(isTrialNoticePending = false)

        goBack()

        assertFalse(navController.currentDestination?.hasRoute<CallScreen>() == true)
    }

    @Test
    fun `opening the pricing from the menu does not stack it on the one that is already up`() {
        endCall(isTrialNoticePending = true)

        composeTestRule.runOnUiThread { navController.navigateToPricing() }
        composeTestRule.waitForIdle()

        // The notice has been shown, so the entry that is up carries on without it, and back
        // leads home rather than to the notice again.
        val route = navController.currentBackStackEntry?.toRoute<PricingScreen>()
        assertFalse(route?.isTrialJustStarted == true)
        goBack()
        assertTrue(navController.currentDestination?.hasRoute<HomeScreen>() == true)
    }

}
