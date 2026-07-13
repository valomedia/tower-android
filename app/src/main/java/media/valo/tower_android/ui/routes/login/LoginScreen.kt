/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.login

//
//  LoginScreen.kt
//  Tower_Android
//

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import media.valo.tower_android.data.local.preferences.profile.DummyProfileDataSource
import media.valo.tower_android.data.local.preferences.profile.ProfileRepository
import media.valo.tower_android.ui.elements.AppBarPreview
import media.valo.tower_android.ui.theme.towerTextStyle

/**
 * Object for the navigation destination for the login screen.
 */
@Serializable
object LoginScreen

/**
 * The screen that prompts the user for their name and e-mail
 *
 * @param modifier      `Modifier` for this element.
 * @param viewModel     `LoginViewModel` dependency.
 * @param navController Used to navigate to the loading screen after credential entry.
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController
) {
    val scrollState = rememberScrollState()

    var isLoading by remember { mutableStateOf(true) }
    var loginFailed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = false

        // If we are back here, despite already having profile information, something went wrong
        // while connecting to the service.
        loginFailed = viewModel.hasProfile()
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 6.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.semantics { isTraversalGroup = true },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Willkommen bei",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            Text(text = "TOWER", style = towerTextStyle, modifier = Modifier.padding(8.dp))
        }
        if (!isLoading) {
            if (loginFailed) {
                ConnectionError(navController = navController)
            } else {
                SignupForm(navController = navController)
            }
        }
    }
}

/**
 * `Preview` for `LoginScreen`.
 */
@Preview(showBackground = true, showSystemUi = true, locale = "de-rDE")
@Composable
fun LoginScreenPreview() {
    AppBarPreview { innerPadding ->
        LoginScreen(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize(),
            viewModel = LoginViewModel(ProfileRepository(DummyProfileDataSource())),
            navController = rememberNavController()
        )
    }
}
