package com.maeldev.conquest.screens.cosplay

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.components.TaskFormContent
import com.maeldev.conquest.data.classes.FormActions
import com.maeldev.conquest.data.classes.TaskFormState
import com.maeldev.conquest.viewmodel.TaskViewModel
import kotlinx.serialization.Serializable

@Serializable
data class EditTask(val taskId: Int)

@Composable
fun EditTask(
    taskId: Int,
    navController: NavController,
    taskViewModel: TaskViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val task by taskViewModel.getTaskById(taskId).collectAsState(initial = null)

    var form by remember { mutableStateOf(TaskFormState()) }
    var baseline by remember { mutableStateOf(TaskFormState()) }
    var notes by remember { mutableStateOf("") }
    var baselineNotes by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(task?.id) {
        task?.let { loaded ->
            form = TaskFormState.fromEntity(loaded)
            baseline = form
            notes = loaded.notes.orEmpty()
            baselineNotes = notes
        }
    }

    TaskFormContent(
        title = "Edit Task",
        form = form,
        notes = notes,
        onFormChange = { form = it },
        onNotesChange = { notes = it },
        actions =
            FormActions(
                snackbarHostState = snackbarHostState,
                isDirty = form != baseline || notes != baselineNotes,
                onCancel = { navController.popBackStack() },
                onCommit = {
                    val current = task ?: return@FormActions
                    taskViewModel.updateTask(
                        form.toUpdatedEntity(
                            current = current,
                            notes = notes.ifBlank { null },
                        ),
                    )
                },
                postCommit = { navController.popBackStack() },
            ),
    )
}
