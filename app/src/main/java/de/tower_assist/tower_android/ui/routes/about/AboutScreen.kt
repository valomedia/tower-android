/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.routes.about

//
//  AboutScreen.kt
//  Tower_Android
//

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import de.tower_assist.tower_android.BuildConfig
import de.tower_assist.tower_android.ui.elements.AppBarPreview

/**
 * Object for the navigation destination for the about screen.
 */
@Serializable
object AboutScreen

/**
 * Screen with detailed version information for the app.
 *
 * @param modifier  `Modifier` for this element.
 */
@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "App Info",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = BuildConfig.APPLICATION_ID,
            onValueChange = {},
            label = { Text("Identifier") },
            readOnly = true,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = BuildConfig.VERSION_NAME,
            onValueChange = {},
            label = { Text("Version") },
            readOnly = true,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = BuildConfig.VERSION_CODE.toString(),
            onValueChange = {},
            label = { Text("Build") },
            readOnly = true,
            modifier = Modifier.padding(8.dp)
        )
    }
}

/**
 * `Preview` for `AboutScreen`.
 */
@Preview(showBackground = true, showSystemUi = true, locale = "de-rDE")
@Composable
fun AboutScreenPreview() {
    AppBarPreview { innerPadding ->
        AboutScreen(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        )
    }
}
