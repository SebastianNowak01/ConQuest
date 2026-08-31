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
import com.maeldev.conquest.viewmodel.CosplayViewModel
import com.maeldev.conquest.components.CosplayFormContent
import com.maeldev.conquest.data.classes.CosplayFormState
import kotlinx.serialization.Serializable

@Serializable
data class EditCosplay(val cosplayId: Int)

@Composable
fun EditCosplay(
    cosplayId: Int,
    navController: NavController,
    cosplayViewModel: CosplayViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val cosplay by cosplayViewModel.getCosplayById(cosplayId).collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    
    var form by remember { mutableStateOf(CosplayFormState()) }
    var originalPhotoPath by remember { mutableStateOf<String?>(null) }
    var didCommit by remember { mutableStateOf(false) }

    LaunchedEffect(cosplay?.uid) {
        cosplay?.let { loaded ->
            form = CosplayFormState.fromEntity(loaded)
            originalPhotoPath = loaded.cosplayPhotoPath
        }
    }

    CosplayFormContent(
        title = "Edit Cosplay",
        form = form,
        originalPhotoPath = originalPhotoPath,
        didCommit = didCommit,
        onFormChange = { form = it },
        snackbarHostState = snackbarHostState,
        onCancel = { navController.popBackStack() },
        onCommit = {
            val current = cosplay ?: return@CosplayFormContent
            val updated = form.toUpdatedEntity(current)
            val oldPath = current.cosplayPhotoPath
            val oldPathToDelete = if (updated.cosplayPhotoPath != oldPath) oldPath else null
            didCommit = true
            cosplayViewModel.updateCosplay(updated, oldPathToDelete = oldPathToDelete)
        },
        postCommit = { navController.popBackStack() }
    )
}
