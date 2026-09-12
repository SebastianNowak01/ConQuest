package com.maeldev.conquest.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.maeldev.conquest.data.pickerMillisToDate
import com.maeldev.conquest.data.toPickerMillis
import com.maeldev.conquest.theme.UIConsts
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

internal fun getCurrentDate(): Date {
    return Calendar.getInstance().time
}

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

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            val selectedMillis = datePickerState.selectedDateMillis
            onDateSelected(selectedMillis?.let { pickerMillisToDate(it) })
            onDismiss()
        }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

/**
 * Date field that opens a picker when tapped.
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

    OutlinedTextField(
        value = selectedDate?.let { convertDateToString(it) } ?: "",
        onValueChange = { },
        label = { Text(label) },
        placeholder = { Text("DD/MM/YYYY") },
        isError = isError,
        supportingText = errorSupportingText(isError, errorMessage),
        trailingIcon = trailing,
        shape = RoundedCornerShape(UIConsts.inputCornerRadius),
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(canClear) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    // Wait for the Final pass so the trailing clear button, which consumes the
                    // release it handles, is not also treated as a tap on the field itself —
                    // otherwise clearing a date would reopen the picker in the same gesture.
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Final)
                    if (upEvent != null && !upEvent.isConsumed) {
                        showModal = true
                    }
                }
            },
        colors = OutlinedTextFieldDefaults.colors(
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
