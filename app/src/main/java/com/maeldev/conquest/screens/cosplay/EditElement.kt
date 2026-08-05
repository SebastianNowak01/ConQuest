package com.maeldev.conquest.screens.cosplay

import com.maeldev.conquest.AppViewModelProvider
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
import com.maeldev.conquest.viewmodel.ElementViewModel
import com.maeldev.conquest.components.ElementFormContent
import com.maeldev.conquest.data.classes.ElementFormState
import kotlinx.serialization.Serializable

@Serializable
data class EditElement(val elementId: Int)

@Composable
fun EditElement(
    elementId: Int,
    navController: NavController,
    elementViewModel: ElementViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val element by elementViewModel.getElementById(elementId).collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    
    var form by remember { mutableStateOf(ElementFormState()) }
    var originalPhotoPath by remember { mutableStateOf<String?>(null) }
    var didCommit by remember { mutableStateOf(false) }

    LaunchedEffect(element?.id) {
        element?.let { loaded ->
            form = ElementFormState.fromEntity(loaded)
            originalPhotoPath = loaded.photoPath
        }
    }

    ElementFormContent(
        title = "Edit Element",
        form = form,
        originalPhotoPath = originalPhotoPath,
        didCommit = didCommit,
        onFormChange = { form = it },
        snackbarHostState = snackbarHostState,
        onCancel = { navController.popBackStack() },
        onCommit = {
            val current = element ?: return@ElementFormContent
            val updated = form.toUpdatedEntity(current)
            val oldPath = current.photoPath
            val oldPathToDelete = if (updated.photoPath != oldPath) oldPath else null
            didCommit = true
            elementViewModel.updateElement(updated, oldPathToDelete = oldPathToDelete)
        },
        postCommit = { navController.popBackStack() }
    )
}
