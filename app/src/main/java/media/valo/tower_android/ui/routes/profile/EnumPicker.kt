/******************************************************************************
 * Copyright (c) 2024-2026 valo.media GmbH                                    *
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

package media.valo.tower_android.ui.routes.profile

//
//  EnumPicker.kt
//  Tower_Android
//

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import media.valo.tower_android.model.Gender
import media.valo.tower_android.ui.theme.TowerTheme
import kotlin.enums.enumEntries


/**
 * A field for selecting a case from a generic `Enum`.
 *
 * This element displays as an `OutlinedTextField` with a `DropdownMenu` to select an `Enum` case.
 *
 * @param value         The value shown in the field.
 * @param onValueChange The callback that is triggered when the value is changed.
 * @param label         The label to be displayed on the field.
 * @param enabled       Whether the date field responds to user input, and appears enabled both
 *                      visually and to accessibility services.
 * @param modifier      `Modifier` for this element.
 */
@Composable
inline fun <reified T : Enum<T>> EnumPicker(
    value: T?,
    crossinline onValueChange: (T?) -> Unit,
    noinline label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(value) { showDropdown = false }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = value?.toString() ?: "Keine Angabe",
            onValueChange = {},
            label = label,
            readOnly = true,
            enabled = enabled,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Option auswählen"
                )
            },
            trailingIcon = {
                if (value != null) {
                    IconButton(
                        onClick = { onValueChange(null) },
                        enabled = enabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Angabe löschen"
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(value) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showDropdown = true
                        }
                    }
                }
        )

        if (enabled) {
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false }
            ) {
                for (t in enumEntries<T>()) {
                    DropdownMenuItem(
                        text = { Text(t.toString()) },
                        onClick = { onValueChange(t) }
                    )
                }
            }
        }
    }
}

/**
 * `Preview` for `EnumPicker`.
 */
@Preview(showBackground = true, locale = "de-rDE")
@Composable
fun EnumPickerPreview() {
    TowerTheme {
        EnumPicker(value = Gender.OTHER, onValueChange = {}, label = { Text("Geschlecht") })
    }
}
