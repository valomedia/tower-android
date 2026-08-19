/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.news

//
//  NewsScreen.kt
//  Tower_Android
//

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import media.valo.tower_android.ui.elements.AppBarPreview

/**
 * Object for the navigation destination for the news screen.
 */
@Serializable
object NewsScreen

/**
 * Screen that shows the user what has changed in the app.
 *
 * The entries are hardcoded (see `newsEntries`) and shown from newest to oldest, so the user can
 * scroll down to read about older versions.
 *
 * @param modifier  `Modifier` for this element.
 */
@Composable
fun NewsScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Neuigkeiten",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        newsEntries.forEachIndexed { index, entry ->
            if (index > 0) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
            NewsEntryItem(entry = entry)
        }
    }
}

/**
 * A single version's worth of news, with a version heading and a bulleted list of changes.
 *
 * @param entry     The `NewsEntry` to show.
 * @param modifier  `Modifier` for this element.
 */
@Composable
private fun NewsEntryItem(
    entry: NewsEntry,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            entry.version,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
        entry.changes.forEach { change ->
            Text(
                "• $change",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
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
