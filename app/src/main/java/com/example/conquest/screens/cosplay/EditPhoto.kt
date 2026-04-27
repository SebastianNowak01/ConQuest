package com.example.conquest.screens.cosplay

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.deleteFileByPath
import com.example.conquest.components.MyImageBox
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyFab
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MyInputField
import com.example.conquest.components.saveImageUriToInternalStorage
import kotlinx.serialization.Serializable
import com.example.conquest.ui.theme.UIConsts

@Serializable
data class EditPhoto(val photoId: Int)

@Composable
fun EditPhoto(
    photoId: Int, navController: NavController, cosplayViewModel: CosplayViewModel = viewModel()
) {
    val context = LocalContext.current
    val photo by cosplayViewModel.getPhotoById(photoId).collectAsState(initial = null)

    var photoPath by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var originalPhotoPath by remember { mutableStateOf("") }
    var didCommit by remember { mutableStateOf(false) }

    DisposableEffect(photoPath, originalPhotoPath, didCommit) {
        onDispose {
            if (!didCommit) {
                photoPath.takeIf { it.isNotBlank() && it != originalPhotoPath }?.let(::deleteFileByPath)
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
                previousUnsavedPath?.let(::deleteFileByPath)
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
        }
    }

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

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = UIConsts.paddingM),
            horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingL)
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
                    photo?.let { current ->
                        val oldPath = current.path
                        val newPath = photoPath.ifEmpty { oldPath }
                        val updated = current.copy(path = newPath, notes = notes.ifBlank { null })
                        val deleteOld = if (newPath != oldPath) oldPath else null
                        didCommit = true
                        cosplayViewModel.updatePhoto(updated, oldPathToDelete = deleteOld)
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
