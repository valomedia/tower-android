/******************************************************************************
 * Copyright (c) 2025-2026.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.outdated

//
//  OutdatedAppVersionScreen.kt
//  Tower_Android
//

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable

/**
 * Object for the navigation destination for the outdated app version screen.
 */
@Serializable
object OutdatedAppVersionScreen

/**
 * The screen that is shown if the backend major version is bigger than that of the app
 *
 * @param modifier `Modifier` for this element.
 */
@Composable
fun OutdatedAppVersionScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
        .verticalScroll(scrollState)
        .padding(horizontal = 6.dp)
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Deine App benötigt ein Update",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "Bitte aktualisiere TOWER Assist über den Play Store, bevor du einen Anruf startest",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "Wir arbeiten kontinuierlich daran, unser Angebot zu verbessen. Gelegentlich ist es dafür notwendig, die App zu aktualisieren." +
                    " Um die App zu aktualisieren, musst du zum Play Store gehen und dort auf „aktualisieren“ tippen. " +
                    "Bevor du deine App aktualisiert hast, kannst du leider keinen Anruf starten.",
            modifier = Modifier.padding(8.dp)
        )
    }
}
