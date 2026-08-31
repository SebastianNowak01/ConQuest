package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
) {
    MyOuterBox {
        MyColumn {
            MyHeaderText(text = title)

            MyInputField(
                value = form.taskName,
                onValueChange = { onFormChange(form.copy(taskName = it)) },
                label = "Task Name*",
                singleLine = true,
            )

            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
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
                onDateSelected = { onFormChange(form.copy(date = it)) })

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
            onCancel = onCancel,
            onCommit = onCommit,
            postCommit = postCommit,
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
