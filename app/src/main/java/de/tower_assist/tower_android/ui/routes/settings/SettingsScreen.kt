/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.routes.settings

//
//  SettingsScreen.kt
//  Tower_Android
//

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.serialization.Serializable
import de.tower_assist.tower_android.data.local.preferences.settings.DummySettingsDataSource
import de.tower_assist.tower_android.data.local.preferences.settings.SettingsRepository
import de.tower_assist.tower_android.ui.elements.AppBarPreview

/**
 * Object for the navigation destination for the settings screen.
 */
@Serializable
object SettingsScreen

/**
 * The screen that allows the user to change application-wide settings.
 *
 * @param viewModel `SettingsViewModel` dependency.
 * @param modifier  `Modifier` for this element.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    var isLoading by remember { mutableStateOf(true) }
    var apiEndpoint by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getApiEndpoint()?.let { apiEndpoint = it }
        isLoading = false
        focusRequester.requestFocus()
    }
    LaunchedEffect(apiEndpoint) { viewModel.setApiEndpoint(apiEndpoint) }

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Einstellungen",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = apiEndpoint,
            onValueChange = { apiEndpoint = it },
            label = { Text("Server") },
            singleLine = true,
            enabled = !isLoading,
            modifier = Modifier.padding(8.dp).focusRequester(focusRequester)
        )
    }
}

/**
 * `Preview` for `SettingsScreen`.
 */
@Preview(showBackground = true, showSystemUi = true, locale = "de-rDE")
@Composable
@SuppressLint("ViewModelConstructorInComposable")
fun SettingsScreenPreview() {
    AppBarPreview { innerPadding ->
        SettingsScreen(
            viewModel = SettingsViewModel(SettingsRepository(DummySettingsDataSource())),
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        )
    }
}
