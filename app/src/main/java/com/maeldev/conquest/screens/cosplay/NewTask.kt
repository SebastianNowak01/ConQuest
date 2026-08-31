package com.maeldev.conquest.screens.cosplay

import com.maeldev.conquest.AppViewModelProvider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.viewmodel.TaskViewModel
import com.maeldev.conquest.components.TaskFormContent
import com.maeldev.conquest.data.classes.TaskFormState
import kotlinx.serialization.Serializable

@Serializable
data class NewTask(val cosplayId: Int)

@Composable
fun NewTask(
    cosplayId: Int,
    navController: NavController,
) {
    val taskViewModel: TaskViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val snackbarHostState = remember { SnackbarHostState() }
    
    var form by remember { mutableStateOf(TaskFormState()) }
    var notes by remember { mutableStateOf("") }

    TaskFormContent(
        title = "Add Task",
        form = form,
        notes = notes,
        onFormChange = { form = it },
        onNotesChange = { notes = it },
        snackbarHostState = snackbarHostState,
        onCancel = { navController.popBackStack() },
        onCommit = {
            taskViewModel.insertTask(form.toEntity(cosplayId = cosplayId, notes = notes.ifBlank { null }))
        },
        postCommit = { navController.popBackStack() }
    )
}
