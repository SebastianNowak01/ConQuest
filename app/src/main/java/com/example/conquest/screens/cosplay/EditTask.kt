package com.example.conquest.screens.cosplay

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.DatePickerFieldToModal
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MySnackbarHost
import com.example.conquest.components.getCurrentDate
import com.example.conquest.data.classes.TaskFormState
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

    LaunchedEffect(task) {
        task?.let {
            form = TaskFormState(
                taskName = it.taskName,
                done = it.done,
                alarm = it.alarm,
                date = it.date ?: getCurrentDate(),
            )
            notes = it.notes ?: ""
        }
    }

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "Edit Task")

            OutlinedTextField(
                value = form.taskName,
                onValueChange = { form = form.copy(taskName = it) },
                label = { Text("Task Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Done switch
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "Done")
                        Switch(
                            checked = form.done,
                            onCheckedChange = { form = form.copy(done = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                uncheckedTrackColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
                // Alarm switch
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "Alarm")
                        Switch(
                            checked = form.alarm,
                            onCheckedChange = { form = form.copy(alarm = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                uncheckedTrackColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }
            DatePickerFieldToModal(
                label = "Task date*",
                selectedDate = form.date,
                onDateSelected = { form = form.copy(date = it) }
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(20.dp),
                maxLines = 6
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            invalidMessage = "Please fill out all required fields!",
            onCancel = { navController.popBackStack() },
            onCommit = {
                val current = task ?: return@MySaveCancelRow
                val updatedTask = form.toEntity(
                    cosplayId = current.cosplayId,
                    id = current.id,
                    notes = notes.ifBlank { null },
                )
                cosplayViewModel.updateTask(updatedTask)
            },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
