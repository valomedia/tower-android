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
//  Created by:
//      * Jean-Pierre Höhmann
//      * mvlexs
//

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
import media.valo.tower_android.data.remote.tower.DummyTowerDataSource
import media.valo.tower_android.data.remote.tower.TowerRepository
import media.valo.tower_android.ui.elements.AppBarPreview
import media.valo.tower_android.ui.elements.Logo
import media.valo.tower_android.ui.routes.home.HomeScreen
import media.valo.tower_android.ui.routes.closed.ClosedScreen
import media.valo.tower_android.ui.routes.login.LoginScreen
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
         * This holds true, when the backends major version is not null and bigger than that of the app.
         *
         * In case of the backendMajorVersion failing to parse and therefore holding '-1', this also holds true, assuming an update is needed.
         *
         * If the backend couldn't be reached and therefore the backendMajorVersion holds null, this holds null as well, indicating connection issues.
         */
        val isAppUpdateNeeded = when{
            backendMajorVersion == null -> null
            backendMajorVersion > appMajorVersion || backendMajorVersion == -1 -> true
            else -> false
            }

        when {
            isAppUpdateNeeded == true -> navController.navigate(route = OutdatedAppVersionScreen) { popUpTo(navController.graph.id) }
            !isConnected || !viewModel.hasProfile() || isServiceOpen == null || schedule == null || isAppUpdateNeeded == null -> navController.navigate(route = LoginScreen) { popUpTo(navController.graph.id) }
            !isServiceOpen -> navController.navigate(route = ClosedScreen(currentSchedule = schedule)) { popUpTo(navController.graph.id) }
            else -> navController.navigate(route = HomeScreen) { popUpTo(navController.graph.id) }
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
fun LoadingScreenPreview() {
    AppBarPreview { innerPadding ->
        LoadingScreen(
            viewModel = LoadingViewModel(
                towerRepository = TowerRepository(DummyTowerDataSource()),
                credentialRepository = CredentialRepository(DummyCredentialDataSource()),
                profileRepository = ProfileRepository(DummyProfileDataSource())
            ),
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize(),
            navController = rememberNavController()
        )
    }
}
