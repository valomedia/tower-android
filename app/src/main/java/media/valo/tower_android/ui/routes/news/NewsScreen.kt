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
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.serialization.Serializable
import media.valo.tower_android.data.local.preferences.settings.DummySettingsDataSource
import media.valo.tower_android.data.local.preferences.settings.SettingsRepository
import media.valo.tower_android.ui.elements.AppBarPreview

/**
 * Object for the navigation destination for the news screen.
 */
@Serializable
object NewsScreen

/**
 * Screen with new changes to the app.
 *
 * @param viewModel `NewsViewModel` dependency.
 * @param modifier  `Modifier` for this element.
 */
@Composable
fun NewsScreen(
    viewModel: NewsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState).padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = "Neuigkeiten",
            modifier = Modifier.padding(8.dp)
        )

        Column(
            horizontalAlignment = AbsoluteAlignment.Left
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = "1.1.0",
                modifier = Modifier.padding(8.dp)
            )
            Text(
                "- Termine können jetzt auch außerhalb der Öffnungszeiten vereinbart werden." + "\n" +
                        "- Die Eingabe der E-Mail Adresse ist verpflichtend." + "\n" +
                        "- Der Kontaktbildschirm ist noch barrierefreier." + "\n" +
                        "- Wenn während einem Anruf der/die Assistent/in die Kamera wechselt oder ein Foto aufnimmt, gibt es einen Signalton." + "\n" +
                        "- Der/Die Assistent/in kann bei einem Anruf die Taschenlampe einschalten, um besser sehen zu können.",
                modifier = Modifier.padding(8.dp)
            )
            /*HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        Text(
            "ältereVersion"
        )
        Text(
            "ältereÄnderungen"
        )*/
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
            viewModel = NewsViewModel(settingsRepository = SettingsRepository(DummySettingsDataSource())),
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
                .fillMaxSize()
        )
    }
}
