package com.example.conquest.screens.cosplay

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyFab
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

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val extension = getFileExtension(context, uri) ?: "jpg"
                val fileName = "cosplay_photo_${System.currentTimeMillis()}.$extension"
                val savedPath = copyUriToInternalStorage(context, uri, fileName)
                photoPath = savedPath
            } catch (e: Exception) {
                error = "Failed to save image: ${e.localizedMessage}"
            }
        }
    }

    LaunchedEffect(photo) {
        photo?.let {
            photoPath = it.path
            notes = it.notes ?: ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MyColumn {
            Text(
                text = "Reference Image",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Box(
                modifier = Modifier
                    .size(UIConsts.defaultHeight)
                    .clip(RoundedCornerShape(UIConsts.editScreenMiscellaneousSize))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (photoPath.isNotEmpty()) {
                    AsyncImage(
                        model = photoPath,
                        contentDescription = "Reference image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(UIConsts.editScreenMiscellaneousSize))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Pick image",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(UIConsts.defaultHeight),
                shape = RoundedCornerShape(UIConsts.editScreenMiscellaneousSize),
                maxLines = 6
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = UIConsts.paddingSize),
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
                    photo?.let { current ->
                        val oldPath = current.path
                        val newPath = photoPath.ifEmpty { oldPath }
                        val updated = current.copy(path = newPath, notes = notes.ifBlank { null })
                        val deleteOld = if (newPath != oldPath) oldPath else null
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
