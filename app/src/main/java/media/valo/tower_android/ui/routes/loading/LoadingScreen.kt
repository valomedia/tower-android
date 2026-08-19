/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.loading

//
//  LoadingScreen.kt
//  Tower_Android
//

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.local.preferences.credentials.DummyCredentialDataSource
import media.valo.tower_android.data.local.preferences.profile.DummyProfileDataSource
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.data.local.preferences.settings.DummySettingsDataSource
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import media.valo.tower_android.data.remote.tower.DummyTowerDataSource
import media.valo.tower_android.data.remote.tower.TowerRepository
import media.valo.tower_android.ui.elements.AppBarPreview
import media.valo.tower_android.ui.elements.Logo
import media.valo.tower_android.ui.routes.home.HomeScreen
import media.valo.tower_android.ui.routes.closed.ClosedScreen
import media.valo.tower_android.ui.routes.login.LoginScreen
import media.valo.tower_android.ui.routes.news.NewsScreen
import media.valo.tower_android.ui.routes.outdated.OutdatedAppVersionScreen

/**
 * Object for the navigation destination for the loading screen.
 */
@Serializable
object LoadingScreen

/**
 * Screen shown while logging in.
 *
 * @param viewModel     `LoadingViewModel` dependency.
 * @param navController Used to navigate onward to either the home or the login screen.
 * @param modifier      `Modifier` for this element.
 */
@Composable
fun LoadingScreen(
    viewModel: LoadingViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier
) {

    LaunchedEffect(Unit) {
        val isConnected = viewModel.connect()
        val isServiceOpen = viewModel.isServiceOpen
        val schedule = viewModel.schedule
        val backendMajorVersion = viewModel.backendMajorVersion
        val appMajorVersion = viewModel.appMajorVersion

        /**
         * Wether the app needs to be updated.
         *
         * The app needs to be updated if:
         * - the backend major version is greater than the app major version
         * - the backend major could not be parsed
         */
        val isAppUpdateNeeded = if (backendMajorVersion != null) {
            backendMajorVersion > appMajorVersion || backendMajorVersion == -1
        } else {null}

        /**
         * Whether there is news the user has not seen yet.
         *
         * This is resolved before navigating, so the news screen can be pushed on top of the home
         * screen without suspending in between.
         */
        val hasUnseenNews = viewModel.hasUnseenNews()

        when {
            isAppUpdateNeeded == true -> navController.navigate(route = OutdatedAppVersionScreen) { popUpTo(navController.graph.id) }
            !isConnected || !viewModel.hasProfile() || isServiceOpen == null || schedule == null || isAppUpdateNeeded == null -> navController.navigate(route = LoginScreen) { popUpTo(navController.graph.id) }
            !isServiceOpen -> navController.navigate(route = ClosedScreen(currentSchedule = schedule)) { popUpTo(navController.graph.id) }
            else -> {
                navController.navigate(route = HomeScreen) { popUpTo(navController.graph.id) }

                // When the app was updated with something new to announce, show the "what's new"
                // screen once. It is pushed on top of the home screen, so going back returns there.
                // The news is only marked as seen here, so it is still shown on the next start when
                // the user did not get this far (such as when they were not logged in yet).
                if (hasUnseenNews) {
                    navController.navigate(route = NewsScreen)
                    viewModel.markNewsSeen()
                }
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Logo(modifier = Modifier.padding(8.dp))
        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
    }
}

/**
 * `Preview` for `LoadingScreen`.
 */
@Preview(showBackground = true, showSystemUi = true, locale = "de-rDE")
@Composable
@SuppressLint("ViewModelConstructorInComposable")
fun LoadingScreenPreview() {
    AppBarPreview { innerPadding ->
        LoadingScreen(
            viewModel = LoadingViewModel(
                towerRepository = TowerRepository(DummyTowerDataSource()),
                credentialRepository = CredentialRepository(DummyCredentialDataSource()),
                profileRepository = ProfileRepository(DummyProfileDataSource()),
                settingsRepository = SettingsRepository(DummySettingsDataSource())
            ),
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize(),
            navController = rememberNavController()
        )
    }
}
