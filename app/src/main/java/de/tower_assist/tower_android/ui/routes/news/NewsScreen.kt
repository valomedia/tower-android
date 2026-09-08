/******************************************************************************
 * Copyright (c) 2024-2026.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.routes.news

//
//  NewsScreen.kt
//  Tower_Android
//

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import de.tower_assist.tower_android.ui.elements.AppBarPreview

/**
 * Object for the navigation destination for the news screen.
 */
@Serializable
object NewsScreen

/**
 * Screen with new changes to the app.
 *
 * @param notes     The release notes to show, newest first.
 * @param modifier  `Modifier` for this element.
 */
@Composable
fun NewsScreen(
    notes: List<ReleaseNotes> = releaseNotes,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = "Neuigkeiten",
            modifier = Modifier.padding(8.dp)
        )

        Column(horizontalAlignment = Alignment.Start) {
            notes.forEachIndexed { index, releaseNotes ->
                if (index > 0) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = releaseNotes.version,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = AnnotatedString.fromHtml(releaseNotes.changes),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

/**
 * `Preview` for `NewsScreen`.
 */
@Preview(showBackground = true, showSystemUi = true, locale = "de-rDE")
@Composable
fun NewsScreenPreview() {
    AppBarPreview { innerPadding ->
        NewsScreen(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        )
    }
}
