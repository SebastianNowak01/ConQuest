package com.maeldev.conquest.screens.cosplay

import com.maeldev.conquest.AppViewModelProvider
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.viewmodel.PhotoViewModel
import com.maeldev.conquest.components.MyImageBox
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MyColumn
import com.maeldev.conquest.components.MyHeaderText
import com.maeldev.conquest.components.MyInputField
import com.maeldev.conquest.components.MySaveCancelRow
import com.maeldev.conquest.components.MySnackbarHost
import com.maeldev.conquest.components.deleteStoredImageByPath
import com.maeldev.conquest.components.rememberDiscardChangesGuard
import com.maeldev.conquest.components.saveImageUriToInternalStorage
import kotlinx.serialization.Serializable
import com.maeldev.conquest.theme.UIConsts

@Serializable
data class EditPhoto(val photoId: Int)

@Composable
fun EditPhoto(
    photoId: Int, navController: NavController, photoViewModel: PhotoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val photo by photoViewModel.getPhotoById(photoId).collectAsState(initial = null)

    var photoPath by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var baselineNotes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var originalPhotoPath by remember { mutableStateOf("") }
    var didCommit by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val latestPhotoPath by rememberUpdatedState(photoPath)
    val latestOriginalPhotoPath by rememberUpdatedState(originalPhotoPath)
    val latestDidCommit by rememberUpdatedState(didCommit)

    DisposableEffect(Unit) {
        onDispose {
            if (!latestDidCommit) {
                latestPhotoPath.takeIf {
                    it.isNotBlank() && it != latestOriginalPhotoPath
                }?.let {
                    deleteStoredImageByPath(
                        context,
                        it
                    )
                }
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            saveImageUriToInternalStorage(
                context = context,
                uri = uri,
                fileNamePrefix = "cosplay_photo",
            ).onSuccess { savedPath ->
                val previousUnsavedPath = photoPath.takeIf {
                    it.isNotBlank() && it != originalPhotoPath && it != savedPath
                }
                previousUnsavedPath?.let {
                    deleteStoredImageByPath(
                        context,
                        it
                    )
                }
                photoPath = savedPath
            }.onFailure { e ->
                error = "Failed to save image: ${e.localizedMessage}"
            }
        }
    }

    LaunchedEffect(photo) {
        photo?.let {
            photoPath = it.path
            originalPhotoPath = it.path
            notes = it.notes ?: ""
            baselineNotes = notes
        }
    }

    val isDirty = photoPath != originalPhotoPath || notes != baselineNotes
    val cancel =
        rememberDiscardChangesGuard(
            isDirty = isDirty,
            onDiscard = { navController.popBackStack() },
        )

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "Edit Photo")

            MyImageBox(
                photoPath = photoPath,
                contentDescription = "Reference image",
                size = UIConsts.heightM,
                shape = RoundedCornerShape(UIConsts.cornerRadiusM),
                clickable = true,
                onClick = { imagePickerLauncher.launch("image/*") },
                previewWhenPhotoExists = true,
                showEditBadge = true,
            )

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            MyInputField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes",
                singleLine = false,
                maxLines = 6,
                height = UIConsts.heightM,
                shape = RoundedCornerShape(UIConsts.cornerRadiusM),
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = true,
            onCancel = cancel,
            onCommit = {
                val current = photo ?: return@MySaveCancelRow
                val oldPath = current.path
                val newPath = photoPath.ifEmpty { oldPath }
                val updated = current.copy(path = newPath, notes = notes.ifBlank { null })
                val deleteOld = if (newPath != oldPath) oldPath else null
                didCommit = true
                photoViewModel.updatePhoto(updated, oldPathToDelete = deleteOld)
            },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
