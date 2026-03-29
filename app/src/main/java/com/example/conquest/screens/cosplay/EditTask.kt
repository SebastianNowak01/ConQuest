package com.example.conquest.screens.cosplay

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.DatePickerFieldToModal
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MyInputField
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MySnackbarHost
import com.example.conquest.components.MySwitchCard
import com.example.conquest.data.classes.TaskFormState
import com.example.conquest.ui.theme.UIConsts
import kotlinx.serialization.Serializable

@Serializable
data class EditTask(val taskId: Int)

@Composable
fun EditTask(
    taskId: Int, navController: NavController, cosplayViewModel: CosplayViewModel = viewModel()
) {
    val task by cosplayViewModel.getTaskById(taskId).collectAsState(initial = null)

    var form by remember { mutableStateOf(TaskFormState()) }
    var notes by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(task?.id) {
        task?.let { loaded ->
            form = TaskFormState.fromEntity(loaded)
            notes = loaded.notes.orEmpty()
        }
    }

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "Edit Task")

            MyInputField(
                value = form.taskName,
                onValueChange = { form = form.copy(taskName = it) },
                label = "Task Name",
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
                    onCheckedChange = { form = form.copy(done = it) },
                    modifier = Modifier.weight(1f)
                )

                MySwitchCard(
                    label = "Alarm",
                    checked = form.alarm,
                    onCheckedChange = { form = form.copy(alarm = it) },
                    modifier = Modifier.weight(1f)
                )
            }
            DatePickerFieldToModal(
                label = "Task date*",
                selectedDate = form.date,
                onDateSelected = { form = form.copy(date = it) })

            MyInputField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes",
                singleLine = false,
                maxLines = 6,
                height = UIConsts.heightM,
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            invalidMessage = "Please fill out all required fields!",
            onCancel = { navController.popBackStack() },
            onCommit = {
                val current = task ?: return@MySaveCancelRow
                cosplayViewModel.updateTask(
                    form.toUpdatedEntity(
                        current = current,
                        notes = notes.ifBlank { null },
                    )
                )
            },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
