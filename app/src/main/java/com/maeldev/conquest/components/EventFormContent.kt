package com.maeldev.conquest.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
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
) {
    MyOuterBox {
        MyColumn {
            MyHeaderText(text = title)

            MyInputField(
                value = form.eventName,
                onValueChange = { onFormChange(form.copy(eventName = it)) },
                label = "Event Name*",
            )

            MyInputField(
                value = form.eventLocation,
                onValueChange = { onFormChange(form.copy(eventLocation = it)) },
                label = "Event Location*",
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
            )
            
            MySwitchCard(
                label = "Reminder",
                checked = form.alarm,
                onCheckedChange = { onFormChange(form.copy(alarm = it)) }
            )

            MyInputField(
                value = form.description,
                onValueChange = { onFormChange(form.copy(description = it)) },
                label = "Description (Optional)",
                singleLine = false,
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onCancel = onCancel,
            onCommit = onCommit,
            postCommit = postCommit,
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
