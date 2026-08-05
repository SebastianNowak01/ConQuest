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
import com.maeldev.conquest.viewmodel.CosplayViewModel
import com.maeldev.conquest.components.CosplayFormContent
import com.maeldev.conquest.data.classes.CosplayFormState
import kotlinx.serialization.Serializable

@Serializable
object NewCosplay

@Composable
fun NewCosplay(
    navController: NavController,
) {
    val cosplayViewModel: CosplayViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val snackbarHostState = remember { SnackbarHostState() }
    
    var form by remember { mutableStateOf(CosplayFormState()) }
    var didCommit by remember { mutableStateOf(false) }

    CosplayFormContent(
        title = "New Project",
        form = form,
        originalPhotoPath = null,
        didCommit = didCommit,
        onFormChange = { form = it },
        snackbarHostState = snackbarHostState,
        onCancel = { navController.popBackStack() },
        onCommit = {
            didCommit = true
            cosplayViewModel.insertCosplay(form.toEntity(uid = 0, finished = false))
        },
        postCommit = { navController.popBackStack() }
    )
}
