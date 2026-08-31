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
import com.maeldev.conquest.viewmodel.ElementViewModel
import com.maeldev.conquest.components.ElementFormContent
import com.maeldev.conquest.data.classes.ElementFormState
import kotlinx.serialization.Serializable

@Serializable
data class NewElement(val cosplayId: Int)

@Composable
fun NewElement(
    cosplayId: Int,
    navController: NavController,
) {
    val elementViewModel: ElementViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val snackbarHostState = remember { SnackbarHostState() }
    
    var form by remember { mutableStateOf(ElementFormState()) }
    var didCommit by remember { mutableStateOf(false) }

    ElementFormContent(
        title = "Add Element",
        form = form,
        originalPhotoPath = null,
        didCommit = didCommit,
        onFormChange = { form = it },
        snackbarHostState = snackbarHostState,
        onCancel = { navController.popBackStack() },
        onCommit = {
            didCommit = true
            elementViewModel.insertElement(
                form.toEntity(cosplayId = cosplayId, id = 0, notes = null)
            )
        },
        postCommit = { navController.popBackStack() }
    )
}
