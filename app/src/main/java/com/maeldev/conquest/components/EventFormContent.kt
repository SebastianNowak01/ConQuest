package com.maeldev.conquest.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.maeldev.conquest.data.classes.EventFormState
import com.maeldev.conquest.data.classes.FormActions

@Composable
fun EventFormContent(
    title: String,
    form: EventFormState,
    onFormChange: (EventFormState) -> Unit,
    actions: FormActions,
) {
    var showErrors by remember { mutableStateOf(false) }
    val cancel = rememberDiscardChangesGuard(isDirty = actions.isDirty, onDiscard = actions.onCancel)

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = title)

            MyInputField(
                value = form.eventName,
                onValueChange = { onFormChange(form.copy(eventName = it)) },
                label = "Event Name*",
                error =
                    FieldError(
                        isError = showErrors && form.eventName.isBlank(),
                        message = "Event name is required",
                    ),
            )

            MyInputField(
                value = form.eventLocation,
                onValueChange = { onFormChange(form.copy(eventLocation = it)) },
                label = "Event Location*",
                error =
                    FieldError(
                        isError = showErrors && form.eventLocation.isBlank(),
                        message = "Event location is required",
                    ),
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
                onCheckedChange = { onFormChange(form.copy(alarm = it)) },
            )

            MyInputField(
                value = form.description,
                onValueChange = { onFormChange(form.copy(description = it)) },
                label = "Description",
                options =
                    InputFieldOptions(
                        singleLine = false,
                    ),
            )
        }

        MySaveCancelRow(
            snackbarHostState = actions.snackbarHostState,
            isValid = form.isValid,
            actions =
                SaveCancelActions(
                    onCancel = cancel,
                    onCommit = actions.onCommit,
                    postCommit = actions.postCommit,
                    onInvalidAttempt = { showErrors = true },
                ),
        )

        MySnackbarHost(hostState = actions.snackbarHostState)
    }
}
