package com.example.conquest.screens.cosplay

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyDeleteFab
import com.example.conquest.components.MyFab
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyPhotoGrid
import com.example.conquest.components.MyPhotoGridItem
import com.example.conquest.components.pickAndSaveImageLauncher
import com.example.conquest.ui.theme.UIConsts

@Composable
fun PhotosTab(navBackStackEntry: NavBackStackEntry, navController: NavController) {
    val args = navBackStackEntry.toRoute<MainCosplayScreen>()
    val context = LocalContext.current
    val cosplayViewModel: CosplayViewModel = viewModel()

    LaunchedEffect(args.uid) {
        cosplayViewModel.setCosplayId(args.uid)
    }

    val photos by cosplayViewModel.photos.collectAsState()
    val gridPhotos = remember(photos) {
        photos.map { photo ->
            MyPhotoGridItem(id = photo.id, path = photo.path)
        }
    }

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

    MyOuterBox {
        if (selectionMode) {
            MyDeleteFab(onClick = {
                cosplayViewModel.deletePhotosByIds(selectedIds)
                selectionMode = false
                selectedIds = emptySet()
            })
        }

        PickAndSaveImage(
            context = context,
            modifier = Modifier.align(Alignment.BottomCenter),
            onImageSaved = { savedPath -> cosplayViewModel.addPhoto(args.uid, savedPath) },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = UIConsts.paddingM)
                .padding(bottom = UIConsts.paddingL * 4),
        ) {
            MyPhotoGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(),
                photos = gridPhotos,
                selectedIds = selectedIds,
                columns = GridCells.Adaptive(minSize = UIConsts.photoThumbSize),
                emptyText = "No images added yet.",
                emptyModifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(),
                contentDescription = "Cosplay photo",
                onItemClick = { photo ->
                    if (!selectionMode) {
                        navController.navigate(EditPhoto(photo.id))
                        return@MyPhotoGrid
                    }
                    val id = photo.id
                    val newSet = if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
                    selectedIds = newSet
                    if (newSet.isEmpty()) selectionMode = false
                },
                onItemLongClick = { photo ->
                    selectionMode = true
                    selectedIds = selectedIds + photo.id
                },
            )
        }
    }
}

@Composable
fun PickAndSaveImage(
    context: Context,
    modifier: Modifier,
    onImageSaved: (String) -> Unit,
) {
    var error by remember { mutableStateOf<String?>(null) }

    val launcher = pickAndSaveImageLauncher(
        context = context,
        fileNamePrefix = "cosplay_photo",
        onSaved = onImageSaved,
        onError = { throwable -> error = "Failed to save image: ${throwable.localizedMessage}" },
    )

    MyFab(
        onClick = { launcher.launch() },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.primary,
        icon = Icons.Default.Add,
        contentDescription = "Add",
    )

    error?.let {
        Text(it, color = MaterialTheme.colorScheme.error)
    }
}

