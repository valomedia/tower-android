/******************************************************************************
 * Copyright (c) 2024.                                                        *
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

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import media.valo.tower_android.ui.elements.AppBarPreview
import media.valo.tower_android.ui.elements.Logo
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
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Logo(modifier = Modifier.padding(8.dp))
        ExtendedFloatingActionButton(
            onClick = {
                navController.navigate(route = CallScreen) { popUpTo(navController.graph.id) }
            },
            icon = { Icon(Icons.Filled.Call, "Jetzt anrufen") },
            text = { Text(text = "Jetzt anrufen") },
            modifier = Modifier.padding(8.dp)
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
        HomeScreen(
            navController = rememberNavController(),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}
