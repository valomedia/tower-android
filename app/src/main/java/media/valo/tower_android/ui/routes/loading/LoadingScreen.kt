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
        navController.navigateToStartupDestination(viewModel.resolveStartupDestination())
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
 * Navigate to where the startup checks have decided the user should go.
 *
 * Every destination replaces the loading screen rather than stacking on top of it, so that going
 * back leaves the app instead of starting over. The news are the exception: they are pushed on top
 * of the home screen, because the app bar gives them a back button rather than the drawer.
 *
 * @param destination   Where the startup checks decided the user should go.
 */
fun NavController.navigateToStartupDestination(destination: StartupDestination) {
    when (destination) {
        StartupDestination.Outdated ->
            navigate(route = OutdatedAppVersionScreen) { popUpTo(graph.id) }
        StartupDestination.Login ->
            navigate(route = LoginScreen) { popUpTo(graph.id) }
        is StartupDestination.Closed ->
            navigate(route = ClosedScreen(currentSchedule = destination.schedule)) {
                popUpTo(graph.id)
            }
        StartupDestination.Home ->
            navigate(route = HomeScreen) { popUpTo(graph.id) }
        StartupDestination.News -> {
            navigate(route = HomeScreen) { popUpTo(graph.id) }
            navigate(route = NewsScreen)
        }
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
                settingsRepository = SettingsRepository(DummySettingsDataSource()),
            ),
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize(),
            navController = rememberNavController()
        )
    }
}
