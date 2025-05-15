/******************************************************************************
 * Copyright (c) 2025.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Wir haben geschlossen",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            currentSchedule.toString() +
            "\nSolltest du einen Termin mit uns vereinbart haben, dann ruf uns gerne trotzdem an.",
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = {
                scope.launch {
                    navController.navigate(route = HomeScreen)
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Okay")
        }
    }

}
