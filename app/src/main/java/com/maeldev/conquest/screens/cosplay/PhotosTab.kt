package com.maeldev.conquest.screens.cosplay

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
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
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.components.MyEmptyState
import com.maeldev.conquest.components.MyFab
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MyPhotoGrid
import com.maeldev.conquest.components.MyPhotoGridActions
import com.maeldev.conquest.components.MyPhotoGridItem
import com.maeldev.conquest.components.MySelectionCountLabel
import com.maeldev.conquest.components.MySelectionModeFabs
import com.maeldev.conquest.components.pickAndSaveImageLauncher
import com.maeldev.conquest.components.rememberSelectionState
import com.maeldev.conquest.theme.UIConsts
import com.maeldev.conquest.viewmodel.PhotoViewModel

@Composable
fun PhotosTab(
    navBackStackEntry: NavBackStackEntry,
    navController: NavController,
) {
    val args = navBackStackEntry.toRoute<MainCosplayScreen>()
    val context = LocalContext.current
    val photoViewModel: PhotoViewModel = viewModel(factory = AppViewModelProvider.Factory)

    LaunchedEffect(args.uid) {
        photoViewModel.setCosplayId(args.uid)
    }

    val photos by photoViewModel.photos.collectAsState()
    val gridPhotos =
        remember(photos) {
            photos.map { photo ->
                MyPhotoGridItem(
                    id = photo.id,
                    path = photo.path,
                    hasNote = !photo.notes.isNullOrBlank(),
                )
            }
        }

    val selection = rememberSelectionState(items = gridPhotos, id = { it.id })

    MyOuterBox {
        if (selection.isActive) {
            MySelectionModeFabs(
                selection = selection,
                itemLabelSingular = "photo",
                itemLabelPlural = "photos",
                onDeleteSelection = { ids -> photoViewModel.deletePhotosByIds(ids) },
            )
        }

        if (!selection.isActive) {
            PickAndSaveImage(
                context = context,
                modifier = Modifier.align(Alignment.BottomCenter),
                onImageSaved = { savedPath -> photoViewModel.addPhoto(args.uid, savedPath) },
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = UIConsts.paddingM)
                    .padding(top = UIConsts.paddingM)
                    .padding(bottom = UIConsts.paddingL * 4),
        ) {
            MyPhotoGrid(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxSize(),
                photos = gridPhotos,
                selectedIds = selection.selectedIds,
                columns = GridCells.Adaptive(minSize = UIConsts.photoThumbSize),
                contentDescription = "Cosplay photo",
                actions =
                    MyPhotoGridActions(
                        onItemClick = { photo ->
                            if (!selection.isActive) {
                                navController.navigate(EditPhoto(photo.id))
                                return@MyPhotoGridActions
                            }
                            selection.toggle(photo.id)
                        },
                        onItemLongClick = { photo -> selection.select(photo.id) },
                    ),
            )
        }

        if (gridPhotos.isEmpty()) {
            MyEmptyState(
                icon = Icons.Default.Image,
                title = "No reference photos",
                hint = "Add the references you are working from.",
                modifier = Modifier.align(Alignment.Center),
            )
        }

        MySelectionCountLabel(selection = selection, itemLabelSingular = "photo")
    }
}

@Composable
fun PickAndSaveImage(
    context: Context,
    modifier: Modifier,
    onImageSaved: (String) -> Unit,
) {
    var error by remember { mutableStateOf<String?>(null) }

    val launcher =
        pickAndSaveImageLauncher(
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
