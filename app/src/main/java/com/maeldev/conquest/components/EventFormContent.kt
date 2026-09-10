package com.maeldev.conquest.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.maeldev.conquest.data.classes.EventFormState

@Composable
fun EventFormContent(
    title: String,
    form: EventFormState,
    onFormChange: (EventFormState) -> Unit,
    snackbarHostState: SnackbarHostState,
    onCancel: () -> Unit,
    onCommit: () -> Unit,
    postCommit: () -> Unit,
    isDirty: Boolean = false,
) {
    var showErrors by remember { mutableStateOf(false) }
    val cancel = rememberDiscardChangesGuard(isDirty = isDirty, onDiscard = onCancel)

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = title)

            MyInputField(
                value = form.eventName,
                onValueChange = { onFormChange(form.copy(eventName = it)) },
                label = "Event Name*",
                isError = showErrors && form.eventName.isBlank(),
                errorMessage = "Event name is required",
            )

            MyInputField(
                value = form.eventLocation,
                onValueChange = { onFormChange(form.copy(eventLocation = it)) },
                label = "Event Location*",
                isError = showErrors && form.eventLocation.isBlank(),
                errorMessage = "Event location is required",
            )

            EventTypeDropdown(
                selectedType = form.eventType,
                onTypeSelected = { type ->
                    type?.let { onFormChange(form.copy(eventType = it)) }
                },
            )

            DatePickerFieldToModal(
                label = "Date*",
                selectedDate = form.eventDate,
                onDateSelected = { onFormChange(form.copy(eventDate = it)) },
                isError = showErrors && form.eventDate == null,
                errorMessage = "Event date is required",
            )
            
            MySwitchCard(
                label = "Reminder",
                checked = form.alarm,
                onCheckedChange = { onFormChange(form.copy(alarm = it)) }
            )

            MyInputField(
                value = form.description,
                onValueChange = { onFormChange(form.copy(description = it)) },
                label = "Description",
                singleLine = false,
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onInvalidAttempt = { showErrors = true },
            onCancel = cancel,
            onCommit = onCommit,
            postCommit = postCommit,
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
