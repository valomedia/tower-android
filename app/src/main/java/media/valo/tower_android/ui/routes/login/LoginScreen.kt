/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.login

//
//  LoginScreen.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import media.valo.tower_android.data.local.preferences.credentials.CredentialRepository
import media.valo.tower_android.data.local.preferences.credentials.DummyCredentialDataSource
import media.valo.tower_android.ui.elements.AppBarPreview
import media.valo.tower_android.ui.routes.loading.LoadingScreen
import media.valo.tower_android.ui.theme.towerTextStyle
import kotlin.let

/**
 * Object for the navigation destination for the login screen.
 */
@Serializable
object LoginScreen

/**
 * The screen that prompts the user for a username and password.
 *
 * @param viewModel     `LoginViewModel` dependency.
 * @param navController Used to navigate to the loading screen after credential entry.
 * @param modifier      `Modifier` for this element.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    var isLoading by remember { mutableStateOf(true) }
    var loginFailed by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getUsername()?.let { username = it }
        viewModel.getPassword()?.let { password = it }
        loginFailed = viewModel.hasCredential()
        viewModel.clearCredential()
        isLoading = false
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(modifier = Modifier.padding(8.dp).semantics { isTraversalGroup = true }) {
            Text(text = "TOWER", style = towerTextStyle)
            if (loginFailed) {
                Text("Anmeldung fehlgeschlagen, bitte erneut versuchen.", color = Color.Red)
            }
        }
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Benutzername") },
            singleLine = true,
            enabled = !isLoading,
            modifier = Modifier.padding(8.dp).focusRequester(focusRequester)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Passwort") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !isLoading,
            modifier = Modifier.padding(8.dp)
        )
        Button(
            enabled = username.isNotBlank() && password.isNotBlank() && !isLoading,
            onClick = {
                scope.launch {
                    isLoading = true
                    viewModel.setUsername(username)
                    viewModel.setPassword(password)
                    navController.navigate(route = LoadingScreen)
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Anmelden")
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
            viewModel = LoginViewModel(CredentialRepository(DummyCredentialDataSource())),
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize(),
            navController = rememberNavController()
        )
    }
}
