package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.maeldev.conquest.data.classes.FormActions
import com.maeldev.conquest.data.classes.TaskFormState
import com.maeldev.conquest.theme.UIConsts

@Composable
fun TaskFormContent(
    title: String,
    form: TaskFormState,
    notes: String,
    onFormChange: (TaskFormState) -> Unit,
    onNotesChange: (String) -> Unit,
    actions: FormActions,
) {
    var showErrors by remember { mutableStateOf(false) }
    val cancel = rememberDiscardChangesGuard(isDirty = actions.isDirty, onDiscard = actions.onCancel)

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = title)

            MyInputField(
                value = form.taskName,
                onValueChange = { onFormChange(form.copy(taskName = it)) },
                label = "Task Name*",
                options =
                    InputFieldOptions(
                        singleLine = true,
                    ),
                error =
                    FieldError(
                        isError = showErrors && form.taskName.isBlank(),
                        message = "Task name is required",
                    ),
            )

            MySectionLabel(text = "Status")
            Row(
                horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingS),
                verticalAlignment = Alignment.CenterVertically,
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
                options =
                    InputFieldOptions(
                        singleLine = false,
                        maxLines = 5,
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
