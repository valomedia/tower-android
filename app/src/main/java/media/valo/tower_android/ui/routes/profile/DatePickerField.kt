/******************************************************************************
 * Copyright (c) 2024-2025.                                                   *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.profile

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import media.valo.tower_android.ui.theme.TowerTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * A date field.
 *
 * This element displays as an `OutlinedTextField` with a date, which the user can change through a
 * `DatePickerModal`.
 *
 * @param value         The date to be shown in the date field.
 * @param onValueChange The callback that is triggered when the date is changed.
 * @param label         The optional label to be displayed on the date field.
 * @param enabled       Whether the date field responds to user input, and appears enabled both
 *                      visually and to accessibility services.
 * @param modifier      `Modifier` for this element.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var showModal by remember { mutableStateOf(false) }

    LaunchedEffect(value) { showModal = false }

    OutlinedTextField(
        value = value?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)) ?: "",
        onValueChange = {},
        label = label,
        readOnly = true,
        enabled = enabled,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Datum auswählen"
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
                        contentDescription = "Datum löschen"
                    )
                }
            }
        },
        modifier = modifier.pointerInput(value) {
            awaitEachGesture {
                awaitFirstDown(pass = PointerEventPass.Initial)
                val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                if (upEvent != null) {
                    showModal = true
                }
            }
        }
    )

    if (enabled && showModal) {
        DatePickerModal(
            value = value,
            onDateSelected = { date -> onValueChange(date) },
            onDismiss = { showModal = false }
        )
    }
}

/**
 * `Preview` for `DatePickerField`.
 */
@Preview(showBackground = true, locale = "de-rDE")
@Composable
fun DatePickerFieldPreview() {
    TowerTheme {
        DatePickerField(value = LocalDate.now(), onValueChange = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    value: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = value?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(
                        datePickerState
                            .selectedDateMillis
                            ?.let { Instant.ofEpochMilli(it) }
                            ?.atZone(ZoneOffset.UTC)
                            ?.toLocalDate()
                    )
                }
            ) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        },
        modifier = modifier
    ) {
        DatePicker(state = datePickerState)
    }
}
