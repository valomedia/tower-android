/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.routes.call_history

//
//  CallHistoryScreen.kt
//  Tower_Android
//

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import de.tower_assist.tower_android.ui.elements.AppBarPreview

/**
 * Object for the navigation destination for the call history screen.
 */
@Serializable
object CallHistoryScreen

/**
 * Screen displaying a log of the previous calls.
 *
 * @param modifier  `Modifier` for this element.
 */
@Composable
fun CallHistoryScreen(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Diese Funktion befindet sich noch in Entwicklung und ist nicht verfügbar.",
        modifier = modifier.padding(8.dp)
    )
}

/**
 * `Preview` for `CallHistoryScreen`.
 */
@Preview(showBackground = true, showSystemUi = true, locale = "de-rDE")
@Composable
fun CallHistoryScreenPreview() {
    AppBarPreview { innerPadding ->
        CallHistoryScreen(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        )
    }
}
