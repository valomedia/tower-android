/******************************************************************************
 * Copyright (c) 2024-2026.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.home

//
//  HomeScreen.kt
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import media.valo.tower_android.billing.SubscriptionUiState
import media.valo.tower_android.billing.SubscriptionViewModel
import media.valo.tower_android.model.SubscriptionStatus
import media.valo.tower_android.ui.elements.AppBarPreview
import media.valo.tower_android.ui.elements.Logo
import media.valo.tower_android.ui.elements.SubscriptionSection
import media.valo.tower_android.ui.elements.openSubscriptionManagement
import media.valo.tower_android.ui.routes.call.CallScreen

/**
 * Object for the navigation destination for the home screen.
 */
@Serializable
object HomeScreen

/**
 * Screen the app starts out on.
 *
 * @param navController Used to navigate to the `CallScreen` when the used starts a call.
 * @param modifier      `Modifier` for this element.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val subscriptionState by subscriptionViewModel.uiState.collectAsState()
    val activity = LocalActivity.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        subscriptionViewModel.refresh()
    }

    if (subscriptionState.isEntitled) {
        ActiveHomeScreen(
            navController = navController,
            subscriptionState = subscriptionState,
            onPurchase = {
                activity?.let(subscriptionViewModel::launchPurchaseFlow)
            },
            onRestore = subscriptionViewModel::restorePurchases,
            onManageSubscription = {
                context.openSubscriptionManagement(subscriptionState.productId)
            },
            modifier = modifier
        )
    } else {
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier.verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
}

@Composable
private fun ActiveHomeScreen(
    navController: NavController,
    subscriptionState: SubscriptionUiState,
    onPurchase: () -> Unit,
    onRestore: () -> Unit,
    onManageSubscription: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp)
        )
        SubscriptionSection(
            subscriptionState = subscriptionState,
            onPurchase = onPurchase,
            onRestore = onRestore,
            onManageSubscription = onManageSubscription
        )
        ExtendedFloatingActionButton(
            onClick = {
                navController.navigate(route = CallScreen) { popUpTo(navController.graph.id) }
            },
            icon = { Icon(Icons.Filled.Call, "Jetzt anrufen") },
            text = { Text(text = "Jetzt anrufen") },
            modifier = Modifier.padding(16.dp)
        )
    }
}

/**
 * `Preview` for `HomeScreen`.
 */
@Preview(showBackground = true, showSystemUi = true, locale = "de-rDE")
@Composable
fun HomeScreenPreview() {
    AppBarPreview { innerPadding ->
        ActiveHomeScreen(
            navController = rememberNavController(),
            subscriptionState = SubscriptionUiState(
                isLoading = false,
                status = SubscriptionStatus.ACTIVE,
                isBillingAvailable = true,
                isProductAvailable = true
            ),
            onPurchase = {},
            onRestore = {},
            onManageSubscription = {},
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "de-rDE")
@Composable
fun PaywallHomeScreenPreview() {
    AppBarPreview { innerPadding ->
        SubscriptionSection(
            subscriptionState = SubscriptionUiState(
                isLoading = false,
                isBillingAvailable = true,
                isProductAvailable = true
            ),
            onPurchase = {},
            onRestore = {},
            onManageSubscription = {},
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        )
    }
}
