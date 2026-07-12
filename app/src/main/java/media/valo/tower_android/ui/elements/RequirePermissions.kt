/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Wrapper for any functionality that requires permissions.
 *
 * This is intended to wrap around a composable for something that requires permissions the user
 * needs to grant. It will ask the user for the necessary permissions and only display the
 * functionality once all necessary permissions are granted.
 *
 * @param permissions   The permissions the user needs to grant in order to proceed.
 * @param rationale     A text that is shown to the user to explain why the permissions are needed.
 * @param modifier      `Modifier` to be applied to the message shown when permissions are missing.
 * @param content       The `Composable` to show once all permissions are granted.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequirePermissions(
    permissions: List<String>,
    rationale: String,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)
) {
    val scrollState = rememberScrollState()
    val permissionStates = rememberMultiplePermissionsState(permissions)

    if (permissionStates.allPermissionsGranted) {
        content()
    } else {
        Column(
            modifier = modifier.verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Logo(modifier = Modifier.padding(8.dp))
            Text(rationale, color = if (permissionStates.shouldShowRationale) Color.Red else Color.Unspecified)
            ExtendedFloatingActionButton(
                onClick = { permissionStates.launchMultiplePermissionRequest() },
                icon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Weiter")},
                text = { Text("Weiter") },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
