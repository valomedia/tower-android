/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.routes.loading

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.tower_assist.tower_android.ui.routes.closed.ClosedScreen
import de.tower_assist.tower_android.ui.routes.home.HomeScreen
import de.tower_assist.tower_android.ui.routes.login.LoginScreen
import de.tower_assist.tower_android.ui.routes.news.NewsScreen
import de.tower_assist.tower_android.ui.routes.outdated.OutdatedAppVersionScreen
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//
//  StartupNavigationRobolectricTest.kt
//  Tower_Android
//

/**
 * Tests for what startup leaves on the back stack.
 *
 * These run the real navigation against the real route objects on a graph of empty destinations,
 * so that no screen's dependencies have to be stood up to check where back leads.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class StartupNavigationRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController

    @Before
    fun setUpNavGraph() {
        composeTestRule.setContent {
            navController = rememberNavController()

            NavHost(navController = navController, startDestination = LoadingScreen) {
                composable<LoadingScreen> { }
                composable<LoginScreen> { }
                composable<HomeScreen> { }
                composable<NewsScreen> { }
                composable<ClosedScreen> { }
                composable<OutdatedAppVersionScreen> { }
            }
        }
        composeTestRule.waitForIdle()
    }

    /**
     * Run the navigation startup would run for a destination.
     *
     * @param destination   The `StartupDestination` to navigate to.
     */
    private fun navigateTo(destination: StartupDestination) {
        composeTestRule.runOnUiThread { navController.navigateToStartupDestination(destination) }
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
    fun `leaves the home screen under the news screen so back leads home`() {
        navigateTo(StartupDestination.News)
        assertTrue(navController.currentDestination?.hasRoute<NewsScreen>() == true)

        goBack()

        assertTrue(navController.currentDestination?.hasRoute<HomeScreen>() == true)
    }

    @Test
    fun `replaces the loading screen when going home`() {
        navigateTo(StartupDestination.Home)
        assertTrue(navController.currentDestination?.hasRoute<HomeScreen>() == true)

        goBack()

        assertFalse(navController.currentDestination?.hasRoute<LoadingScreen>() == true)
    }

    @Test
    fun `replaces the loading screen when held at a gate`() {
        navigateTo(StartupDestination.Login)
        assertTrue(navController.currentDestination?.hasRoute<LoginScreen>() == true)

        goBack()

        assertFalse(navController.currentDestination?.hasRoute<LoadingScreen>() == true)
    }

}
