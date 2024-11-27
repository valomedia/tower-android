/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.home

//
//  CallButton.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import media.valo.tower_android.ui.theme.TowerTheme

/**
 * Button for starting a call.
 *
 * @param snackbarHostState Global state of the snackbar host, which is used to show a snackbar.
 * @param modifier          `Modifier` for this element.
 */
@Composable
fun CallButton(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    ExtendedFloatingActionButton(
        onClick = {
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Diese Funktion befindet sich noch in Entwicklung und ist nicht verfügbar."
                )
            }
        },
        icon = { Icon(Icons.Filled.Call, "Jetzt anrufen") },
        text = { Text(text = "Jetzt anrufen") },
        modifier = modifier
    )
}

/**
 * `Preview` for `CallButton`.
 */
@Preview(showBackground = true, locale = "de-rDE")
@Composable
fun CallButtonPreview() {
    TowerTheme {
        CallButton(snackbarHostState = remember { SnackbarHostState() })
    }
}
