package com.maeldev.conquest.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.maeldev.conquest.data.pickerMillisToDate
import com.maeldev.conquest.data.startOfDay
import com.maeldev.conquest.data.toPickerMillis
import com.maeldev.conquest.theme.UIConsts
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

internal fun getCurrentDate(): Date {
    return Calendar.getInstance().time.startOfDay()
}

/**
 * The calendar dialog in the app's own colours.
 *
 * Left alone it arrives as a pale lilac panel: Material derives the dialog's container and its
 * selected-day colours from the *baseline* palette, not from this app's scheme, so the one screen
 * element the user is asked to concentrate on belongs to no other control. The selected day is
 * given the same green-on-light pairing the status row uses for its active segment.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun appDatePickerColors(): DatePickerColors =
    DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        headlineContentColor = MaterialTheme.colorScheme.onBackground,
        weekdayContentColor = MaterialTheme.colorScheme.onBackground,
        subheadContentColor = MaterialTheme.colorScheme.onBackground,
        navigationContentColor = MaterialTheme.colorScheme.onBackground,
        yearContentColor = MaterialTheme.colorScheme.onBackground,
        currentYearContentColor = MaterialTheme.colorScheme.onBackground,
        selectedYearContentColor = MaterialTheme.colorScheme.primary,
        selectedYearContainerColor = MaterialTheme.colorScheme.secondary,
        dayContentColor = MaterialTheme.colorScheme.onBackground,
        selectedDayContentColor = MaterialTheme.colorScheme.primary,
        selectedDayContainerColor = MaterialTheme.colorScheme.secondary,
        todayContentColor = MaterialTheme.colorScheme.onBackground,
        todayDateBorderColor = MaterialTheme.colorScheme.secondary,
        dividerColor = MaterialTheme.colorScheme.outline,
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Date?) -> Unit,
    onDismiss: () -> Unit,
    initialDate: Date? = null,
) {
    // Open on the date the field already holds, instead of always landing on today.
    val initialMillis = initialDate?.toPickerMillis()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    val colors = appDatePickerColors()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = colors,
        shape = RoundedCornerShape(UIConsts.cornerRadiusL),
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    onDateSelected(selectedMillis?.let { pickerMillisToDate(it) })
                    onDismiss()
                },
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    ) {
        DatePicker(state = datePickerState, colors = colors)
    }
}

/**
 * Date field that opens the calendar picker when tapped.
 *
 * The field is [readOnly] and opens the dialog from its own press interactions. Left writable it
 * takes focus and raises the keyboard on a tap, so the field could only be typed into and the
 * calendar was unreachable — the picker is the point of the control. Reading presses off the
 * field's own interaction source also keeps the clear button separate: an [IconButton] owns its
 * presses, so taking a date back off no longer reopens the dialog in the same gesture.
 *
 * An optional field takes [onClear], which puts a clear button in place of the calendar icon
 * while a date is set — without it a date, once chosen, can never be taken back off.
 */
@Composable
fun DatePickerFieldToModal(
    label: String,
    selectedDate: Date?,
    onDateSelected: (Date?) -> Unit,
    onClear: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    var showModal by remember { mutableStateOf(false) }
    val canClear = onClear != null && selectedDate != null
    val calendarIcon: @Composable () -> Unit = {
        Icon(Icons.Default.DateRange, contentDescription = "Select date")
    }
    val trailing = clearTrailingIcon(canClear, label, onClear) ?: calendarIcon
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showModal = true
            }
        }
    }

    OutlinedTextField(
        value = selectedDate?.let { convertDateToString(it) } ?: "",
        onValueChange = { },
        readOnly = true,
        label = { Text(label) },
        placeholder = { Text("DD/MM/YYYY") },
        isError = isError,
        supportingText = errorSupportingText(isError, errorMessage),
        trailingIcon = trailing,
        shape = RoundedCornerShape(UIConsts.inputCornerRadius),
        interactionSource = interactionSource,
        modifier = Modifier.fillMaxWidth(),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                errorContainerColor = MaterialTheme.colorScheme.background,
            ),
    )

    if (showModal) {
        DatePickerModal(
            onDateSelected = {
                onDateSelected(it)
                showModal = false
            },
            onDismiss = { showModal = false },
            initialDate = selectedDate,
        )
    }
}

fun convertDateToString(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(date)
}
