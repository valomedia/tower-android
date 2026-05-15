/******************************************************************************
 * Copyright (c) 2024-2026.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.settings

//
//  SettingsScreen.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.serialization.Serializable
import media.valo.tower_android.billing.SubscriptionUiState
import media.valo.tower_android.billing.SubscriptionViewModel
import media.valo.tower_android.model.SubscriptionStatus
import media.valo.tower_android.ui.elements.AppBarPreview
import media.valo.tower_android.ui.elements.SubscriptionSection
import media.valo.tower_android.ui.elements.openSubscriptionManagement

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
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val activity = LocalActivity.current
    val context = LocalContext.current
    val subscriptionState by subscriptionViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(true) }
    var apiEndpoint by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getApiEndpoint()?.let { apiEndpoint = it }
        subscriptionViewModel.refresh()
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
        SubscriptionSection(
            subscriptionState = subscriptionState,
            onPurchase = {
                activity?.let(subscriptionViewModel::launchPurchaseFlow)
            },
            onRestore = subscriptionViewModel::restorePurchases,
            onManageSubscription = {
                context.openSubscriptionManagement(subscriptionState.productId)
            }
        )
    }
}

/**
 * `Preview` for `SettingsScreen`.
 */
@Preview(showBackground = true, showSystemUi = true, locale = "de-rDE")
@Composable
fun SettingsScreenPreview() {
    AppBarPreview { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Text(
                "Einstellungen",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = "https://tower.example",
                onValueChange = {},
                label = { Text("Server") },
                modifier = Modifier.padding(8.dp)
            )
            SubscriptionSection(
                subscriptionState = SubscriptionUiState(
                    isLoading = false,
                    status = SubscriptionStatus.ACTIVE,
                    isBillingAvailable = true,
                    isProductAvailable = true
                ),
                onPurchase = {},
                onRestore = {},
                onManageSubscription = {}
            )
        }
    }
}
