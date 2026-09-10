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
import com.maeldev.conquest.components.InputFieldOptions
import com.maeldev.conquest.components.MyColumn
import com.maeldev.conquest.components.MyHeaderText
import com.maeldev.conquest.components.MyImageBox
import com.maeldev.conquest.components.MyImageBoxConfig
import com.maeldev.conquest.components.MyInputField
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MySaveCancelRow
import com.maeldev.conquest.components.MySnackbarHost
import com.maeldev.conquest.components.SaveCancelActions
import com.maeldev.conquest.components.rememberDiscardChangesGuard
import com.maeldev.conquest.theme.UIConsts
import com.maeldev.conquest.viewmodel.ProgressPhotoViewModel
import kotlinx.serialization.Serializable

@Serializable
data class EditProgressPhoto(
    val photoId: Int,
    val cosplayId: Int,
)

@Composable
fun EditProgressPhoto(
    photoId: Int,
    cosplayId: Int,
    navController: NavController,
    progressPhotoViewModel: ProgressPhotoViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val photo by progressPhotoViewModel.getProgressPhotoById(photoId, cosplayId).collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    var notes by remember { mutableStateOf("") }
    var baselineNotes by remember { mutableStateOf("") }

    LaunchedEffect(photo?.id) {
        notes = photo?.notes.orEmpty()
        baselineNotes = notes
    }

    val cancel =
        rememberDiscardChangesGuard(
            isDirty = notes != baselineNotes,
            onDiscard = { navController.popBackStack() },
        )

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "Edit Progress Note")

            MyImageBox(
                photoPath = photo?.path.orEmpty(),
                clickable = true,
                onClick = {},
                config =
                    MyImageBoxConfig(
                        size = UIConsts.heightM,
                        contentDescription = "Progress photo",
                        previewWhenPhotoExists = true,
                    ),
            )

            MyInputField(
                value = notes,
                onValueChange = { notes = it },
                label = "Note",
                onClear = { notes = "" },
                options =
                    InputFieldOptions(
                        singleLine = false,
                        maxLines = 6,
                        height = UIConsts.heightM,
                    ),
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = true,
            actions =
                SaveCancelActions(
                    onCancel = cancel,
                    onCommit = {
                        val current = photo ?: return@SaveCancelActions
                        progressPhotoViewModel.updateProgressPhoto(current.copy(notes = notes.ifBlank { null }))
                    },
                    postCommit = { navController.popBackStack() },
                ),
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
