/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.closed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import media.valo.tower_android.ui.routes.home.HomeScreen

//
//  ConnectionError.kt
//  Tower_Android
//
//  Created by:
//      * mvlexs
//

/**
 * Object for the navigation destination for the currently closed screen.
 */
@Serializable
data class ClosedScreen(val currentSchedule: String)

/**
 * A message telling the user that they are trying to reach us outside our opening hours.
 *
 * @param modifier      Modifier for this element.
 * @param navController User to navigate to the loading screen when the user chooses to retry.
 */
@Composable
fun ClosedScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    currentSchedule: String
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState).fillMaxSize().padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Wir haben gerade geschlossen",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            currentSchedule,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            "Wir arbeiten daran, diese Zeiten weiter auszubauen. Falls du einen Termin mit uns hast, kannst du trotzdem einen Anruf mit uns starten.",
            modifier = Modifier.padding(8.dp)
        )
        Button(
            onClick = {
                scope.launch {
                    navController.navigate(route = HomeScreen)
                }
            },
            modifier = Modifier.padding(8.dp),
        ) {
            Text("Weiter")
        }
    }

}
