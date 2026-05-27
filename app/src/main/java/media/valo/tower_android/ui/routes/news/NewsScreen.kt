/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.news

//
//  NewsScreen.kt
//  Tower_Android
//
//  Created by:
//      * Jan Hofherr
//

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
 * Screen with new changes to the app.
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
        /*Text(
            "Version1"
        )
        Text(
            "Änderungen1"
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        Text(
            "Version2"
        )
        Text(
            "Änderungen2"
        )*/
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
