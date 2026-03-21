package com.example.conquest.screens.cosplay

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.conquest.components.MyBox
import com.example.conquest.components.MyDeleteFab
import com.example.conquest.components.MyFab
import com.example.conquest.data.entity.CosplayPhoto
import java.io.File
import java.io.InputStream
import java.io.OutputStream

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

    MyBox {
        if (selectionMode) {
            MyDeleteFab(onClick = {
                cosplayViewModel.deletePhotosByIds(selectedIds)
                selectionMode = false
                selectedIds = emptySet()
            })
        }

        PickAndSaveImage(context, Modifier.align(Alignment.BottomCenter)) { savedPath ->
            cosplayViewModel.addPhoto(args.uid, savedPath)
        }

        Spacer(modifier = Modifier.height(24.dp))

        CosplayPhotoList(photos = photos, selectedIds = selectedIds, onItemClick = { photo ->
            if (selectionMode) {
                val id = photo.id
                val newSet = if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
                selectedIds = newSet
                if (newSet.isEmpty()) selectionMode = false
            } else {
                navController.navigate(EditPhoto(photo.id))
            }
        }, onItemLongClick = { photo ->
            selectionMode = true
            selectedIds = selectedIds + photo.id
        })
    }
}

fun getFileExtension(context: Context, uri: Uri): String? {
    val contentResolver: ContentResolver = context.contentResolver
    val mimeTypeMap = MimeTypeMap.getSingleton()
    val mimeType = contentResolver.getType(uri)
    return mimeTypeMap.getExtensionFromMimeType(mimeType)
}

@Composable
fun PickAndSaveImage(
    context: Context, modifier: Modifier, onImageSaved: (String) -> Unit,
) {
    var error by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val extension = getFileExtension(context, uri) ?: "jpg"
                val fileName = "cosplay_photo_${System.currentTimeMillis()}.$extension"
                val savedPath = copyUriToInternalStorage(context, uri, fileName)
                onImageSaved(savedPath)
            } catch (e: Exception) {
                error = "Failed to save image: ${e.localizedMessage}"
            }
        }
    }

    MyFab(
        onClick = { launcher.launch("image/*") },
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

fun copyUriToInternalStorage(context: Context, uri: Uri, fileName: String): String {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val file = File(context.filesDir, fileName)
    val outputStream: OutputStream = file.outputStream()

    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
    return file.absolutePath
}

@Composable
fun CosplayPhotoList(
    photos: List<CosplayPhoto>,
    selectedIds: Set<Int>,
    onItemClick: (CosplayPhoto) -> Unit,
    onItemLongClick: (CosplayPhoto) -> Unit
) {
    if (photos.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), contentAlignment = Alignment.Center
        ) {
            Text("No images added yet.")
        }
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
