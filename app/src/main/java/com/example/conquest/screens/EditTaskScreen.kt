package com.example.conquest.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyFab
import com.example.conquest.components.getCurrentDate
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class EditTaskScreen(val taskId: Int)

@Composable
fun EditTaskScreen(
    taskId: Int, navController: NavController, cosplayViewModel: CosplayViewModel = viewModel()
) {
    val task by cosplayViewModel.getTaskById(taskId).collectAsState(initial = null)

    var taskName by remember { mutableStateOf("") }
    var done by remember { mutableStateOf(false) }
    var alarm by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    var date: Date? by remember { mutableStateOf(task?.date ?: getCurrentDate()) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(task) {
        task?.let {
            taskName = it.taskName
            done = it.done
            alarm = it.alarm
            notes = it.notes ?: ""
            date = it.date ?: getCurrentDate()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Edit Task",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
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
                            checked = done,
                            onCheckedChange = { done = it },
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
                            checked = alarm,
                            onCheckedChange = { alarm = it },
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
            com.example.conquest.components.DatePickerFieldToModal(
                label = "Task date*",
                selectedDate = date,
                onDateSelected = { date = it }
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
            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            MyFab(
                onClick = { navController.popBackStack() },
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.Close,
                contentDescription = "Discard"
            )
            MyFab(
                onClick = {
                    task?.let {
                        val updatedTask = it.copy(
                            taskName = taskName,
                            done = done,
                            alarm = alarm,
                            notes = notes,
                            date = date
                        )
                        cosplayViewModel.updateTask(updatedTask)
                    }
                    navController.popBackStack()
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.Check,
                contentDescription = "Save"
            )
        }
    }
}
