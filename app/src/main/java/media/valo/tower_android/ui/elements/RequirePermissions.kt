/******************************************************************************
 * Copyright (c) 2024.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

//
//  RequirePermissions.kt
//  Tower_Android
//
//  Created by:
//      * Jean-Pierre Höhmann
//

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
            Logo()
            Text(rationale, color = if (permissionStates.shouldShowRationale) Color.Red else Color.Unspecified)
            ExtendedFloatingActionButton(
                onClick = { permissionStates.launchMultiplePermissionRequest() },
                icon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Weiter")},
                text = { Text("Weiter") }
            )
        }
    }
}
