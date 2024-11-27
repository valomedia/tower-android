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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.serialization.Serializable
import media.valo.tower_android.ui.elements.AppBarPreview
import media.valo.tower_android.ui.elements.Logo

/**
 * Object for the navigation destination for the home screen.
 */
@Serializable
object HomeScreen

/**
 * Screen the app starts out on.
 *
 * @param snackbarHostState Global state of the snackbar host, which is used to show a snackbar.
 * @param modifier          `Modifier` for this element.
 */
@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Logo()
        CallButton(snackbarHostState = snackbarHostState)
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
            snackbarHostState = remember { SnackbarHostState() },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}
