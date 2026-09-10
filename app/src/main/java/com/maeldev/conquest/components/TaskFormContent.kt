package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.maeldev.conquest.data.classes.TaskFormState
import com.maeldev.conquest.theme.UIConsts

@Composable
fun TaskFormContent(
    title: String,
    form: TaskFormState,
    notes: String,
    onFormChange: (TaskFormState) -> Unit,
    onNotesChange: (String) -> Unit,
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
                value = form.taskName,
                onValueChange = { onFormChange(form.copy(taskName = it)) },
                label = "Task Name*",
                singleLine = true,
                isError = showErrors && form.taskName.isBlank(),
                errorMessage = "Task name is required",
            )

            MySectionLabel(text = "Status")
            Row(
                horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingS),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MySwitchCard(
                    label = "Done",
                    checked = form.done,
                    onCheckedChange = { onFormChange(form.copy(done = it)) },
                    modifier = Modifier.weight(1f),
                )
                MySwitchCard(
                    label = "Alarm",
                    checked = form.alarm,
                    onCheckedChange = { onFormChange(form.copy(alarm = it)) },
                    modifier = Modifier.weight(1f),
                )
            }

            DatePickerFieldToModal(
                label = "Task date*",
                selectedDate = form.date,
                onDateSelected = { onFormChange(form.copy(date = it)) },
                isError = showErrors && form.date == null,
                errorMessage = "Task date is required",
            )

            MyInputField(
                value = notes,
                onValueChange = onNotesChange,
                label = "Notes",
                singleLine = false,
                maxLines = 5,
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
