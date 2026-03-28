package com.example.conquest.screens.cosplay

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.pickAndSaveImageLauncher
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyDeleteFab
import com.example.conquest.components.MyFab
import com.example.conquest.data.entity.CosplayPhoto

@Composable
fun PhotosTab(navBackStackEntry: NavBackStackEntry, navController: NavController) {
    val args = navBackStackEntry.toRoute<MainCosplayScreen>()
    val context = LocalContext.current
    val cosplayViewModel: CosplayViewModel = viewModel()

    LaunchedEffect(args.uid) {
        cosplayViewModel.setCosplayId(args.uid)
    }

    val photos by cosplayViewModel.photos.collectAsState()

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
            onImageSaved = { savedPath -> cosplayViewModel.addPhoto(args.uid, savedPath) })

        Spacer(modifier = Modifier.height(24.dp))

        CosplayPhotoList(photos = photos, selectedIds = selectedIds, onItemClick = { photo ->
            if (!selectionMode) {
                navController.navigate(EditPhoto(photo.id))
                return@CosplayPhotoList
            }
            val id = photo.id
            val newSet = if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
            selectedIds = newSet
            if (newSet.isEmpty()) selectionMode = false
        }, onItemLongClick = { photo ->
            selectionMode = true
            selectedIds = selectedIds + photo.id
        })
    }
}

@Composable
fun PickAndSaveImage(
    context: Context, modifier: Modifier, onImageSaved: (String) -> Unit,
) {
    var error by remember { mutableStateOf<String?>(null) }

    val launcher = pickAndSaveImageLauncher(
        context = context,
        fileNamePrefix = "cosplay_photo",
        onSaved = onImageSaved,
        onError = { t -> error = "Failed to save image: ${t.localizedMessage}" })

    MyFab(
        onClick = { launcher.launch() },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.primary,
        icon = Icons.Default.Add,
        contentDescription = "Add"
    )

    error?.let {
        Text(it, color = MaterialTheme.colorScheme.error)
    }
}


@Composable
fun CosplayPhotoList(
    photos: List<CosplayPhoto>,
    selectedIds: Set<Int>,
    onItemClick: (CosplayPhoto) -> Unit,
    onItemLongClick: (CosplayPhoto) -> Unit
) {
    if (photos.isEmpty()) {
        PlaceholderBox()
    } else {
        LazyRow(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = photos, key = { it.id }) { photo ->
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .combinedClickable(
                            onClick = { onItemClick(photo) },
                            onLongClick = { onItemLongClick(photo) }),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedIds.contains(photo.id)) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.background
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    AsyncImage(
                        model = photo.path,
                        contentDescription = "Cosplay photo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceholderBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp), contentAlignment = Alignment.Center
    ) {
        Text("No images added yet.")
    }
}