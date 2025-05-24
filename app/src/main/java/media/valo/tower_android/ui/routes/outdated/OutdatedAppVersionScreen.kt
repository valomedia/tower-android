/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.outdated

//
//  OutdatedAppVersionScreen.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
 * @param modifier``Modifier` for this element.
 */
@Composable
fun OutdatedAppVersionScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Veraltete Version",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = "Die Version deiner App ist veraltet. Aktualisiere bitte deine Tower Assistenz App auf die neuste Version, um technische Fehler zu vermeiden.",
            onValueChange = {},
            modifier = Modifier.padding(8.dp)
        )
    }
}
