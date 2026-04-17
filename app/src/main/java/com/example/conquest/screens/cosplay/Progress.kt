package com.example.conquest.screens.cosplay

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyDeleteFab
import com.example.conquest.components.MyFab
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyPhotoGrid
import com.example.conquest.components.MyPhotoGridItem
import com.example.conquest.components.pickAndSaveImageLauncher
import com.example.conquest.components.saveBitmapToInternalStorage
import com.example.conquest.ui.theme.UIConsts
import kotlinx.serialization.Serializable

@Serializable
data class Progress(val cosplayId: Int)

@Composable
fun ProgressScreen(
    navController: NavController,
    cosplayId: Int,
    cosplayViewModel: CosplayViewModel = viewModel(),
) {
    val context = LocalContext.current
    val photos by cosplayViewModel.progressPhotos.collectAsState()
    val gridPhotos = remember(photos) {
        photos.map { photo ->
            MyPhotoGridItem(id = photo.id, path = photo.path)
        }
    }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(cosplayId) {
        cosplayViewModel.setProgressCosplayId(cosplayId)
    }

    val galleryLauncher = pickAndSaveImageLauncher(
        context = context,
        fileNamePrefix = "progress_photo",
        onSaved = { path -> cosplayViewModel.addProgressPhoto(cosplayId, path) },
        onError = { throwable -> error = "Failed to save image: ${throwable.localizedMessage}" },
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap ->
        if (bitmap == null) {
            return@rememberLauncherForActivityResult
        }
        saveBitmapToInternalStorage(
            context = context,
            bitmap = bitmap,
            fileNamePrefix = "progress_photo",
        ).onSuccess { path ->
            cosplayViewModel.addProgressPhoto(cosplayId, path)
        }.onFailure { throwable ->
            error = "Failed to save image: ${throwable.localizedMessage}"
        }
    }

    MyOuterBox {
        if (selectionMode) {
            MyDeleteFab(
                onClick = {
                    cosplayViewModel.deleteProgressPhotosByIds(selectedIds)
                    selectionMode = false
                    selectedIds = emptySet()
                },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = UIConsts.paddingM)
                .padding(bottom = UIConsts.paddingL * 4),
        ) {
            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            if (photos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No progress photos yet")
                }
            } else {
                MyPhotoGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(),
                    photos = gridPhotos,
                    selectedIds = selectedIds,
                    columns = GridCells.Adaptive(minSize = UIConsts.photoThumbSize),
                    contentPadding = PaddingValues(),
                    emptyText = "No progress photos yet",
                    contentDescription = "Progress photo",
                    onItemClick = { photo ->
                        if (!selectionMode) {
                            navController.navigate(EditProgressPhoto(photo.id, cosplayId))
                            return@MyPhotoGrid
                        }
                        val id = photo.id
                        selectedIds = if (selectedIds.contains(id)) {
                            selectedIds - id
                        } else {
                            selectedIds + id
                        }
                        if (selectedIds.isEmpty()) {
                            selectionMode = false
                        }
                    },
                    onItemLongClick = { photo ->
                        selectionMode = true
                        selectedIds = selectedIds + photo.id
                    },
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = UIConsts.paddingM),
            horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingL),
        ) {
            MyFab(
                onClick = { galleryLauncher.launch() },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.Add,
                contentDescription = "Add from gallery",
            )
            MyFab(
                onClick = { cameraLauncher.launch(null) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.PhotoCamera,
                contentDescription = "Take photo",
            )
        }
    }
}
