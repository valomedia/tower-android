/******************************************************************************
 * Copyright (c) 2025-2026 valo.media GmbH                                    *
 * All rights reserved.                                                       *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU Affero General Public License as             *
 * published by the Free Software Foundation, either version 3 of the         *
 * License, or (at your option) any later version.                            *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Affero General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Affero General Public License   *
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.     *
 ******************************************************************************/

package de.tower_assist.tower_android.ui.routes.closed

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import de.tower_assist.tower_android.ui.routes.home.HomeScreen
import de.tower_assist.tower_android.ui.screens.closed.ClosedScreenViewModel

//
//  ConnectionError.kt
//  Tower_Android
//

/**
 * Object for the navigation destination for the currently closed screen.
 */
@Serializable
data class ClosedScreen(val currentSchedule: String)

/**
 * A message telling the user that they are trying to reach us outside our opening hours.
 *
 * @param modifier          Modifier for this element.
 * @param navController     Used to navigate to the home screen when the user chooses to continue.
 * @param currentSchedule   A human-readable string of the current opening hours.
 * @param userFirstName     Prefill first name in the booking URL.
 * @param userEmail         Prefill e-mail in the booking URL.
 */
@Composable
fun ClosedScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    currentSchedule: String,
    userFirstName: String? = null,
    userEmail: String? = null,
    viewModel: ClosedScreenViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(horizontal = 6.dp),
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
            "Wir arbeiten daran, diese Zeiten weiter auszubauen. Wenn du jetzt einen Termin mit uns hast, gehe auf Weiter. Ansonsten kannst du hier direkt deinen persönlichen Termin vereinbaren.",
            modifier = Modifier.padding(8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            // Secondary action: continue
            TextButton(
                onClick = { navController.navigate(route = HomeScreen) },
                modifier = Modifier.semantics { traversalIndex = 1f }
            ) { Text("Weiter") }

            Spacer(modifier = Modifier.width(8.dp))

            // Primary action: book an appointment
            Button(
                onClick = {
                    scope.launch {
                        val email = userEmail ?: viewModel.getEmail()
                        val firstName = userFirstName ?: viewModel.getFirstName()

                        val uri = "https://tower-assist.de/terminvereinbarung/"
                            .toUri()
                            .buildUpon()
                            .apply {
                                if (!email.isNullOrBlank())  appendQueryParameter("email", email)
                                if (!firstName.isNullOrBlank())  appendQueryParameter("firstname", firstName)
                            }
                            .build()

                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                },
                modifier = Modifier.semantics { traversalIndex = 0f }
            ) { Text("Termin vereinbaren") }
        }
    }

}
