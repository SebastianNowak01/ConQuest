package com.example.conquest.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import com.example.conquest.CosplayViewModel
import com.example.conquest.data.entity.CosplayPhoto
import kotlinx.serialization.Serializable
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import coil.compose.AsyncImage

@Serializable
data class MainCosplayScreen(
    val uid: Int
)


@Composable
fun MainCosplayScreen(
    navBackStackEntry: NavBackStackEntry
) {
    val args = navBackStackEntry.toRoute<MainCosplayScreen>()
    val context = LocalContext.current
    val cosplayViewModel: CosplayViewModel = viewModel()

    LaunchedEffect(args.uid) {
        cosplayViewModel.setCosplayId(args.uid)
    }

    val photos by cosplayViewModel.photos.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Cosplay UID: ${args.uid}")

        Spacer(modifier = Modifier.height(24.dp))

        PickAndSaveImage(context) { savedPath ->
            cosplayViewModel.addPhoto(args.uid, savedPath)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("All Photos:")
        CosplayPhotoList(photos)
    }
}

@Composable
fun PickAndSaveImage(
    context: Context, onImageSaved: (String) -> Unit
) {
    var error by remember { mutableStateOf<String?>("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val fileName = "cosplay_photo_${System.currentTimeMillis()}.jpg"
                val savedPath = copyUriToInternalStorage(context, uri, fileName)
                onImageSaved(savedPath)
            } catch (e: Exception) {
                error = "Failed to save image: ${e.localizedMessage}"
            }
        }
    }

    Button(onClick = { launcher.launch("image/*") }) {
        Text("Pick Photo")
    }

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
fun CosplayPhotoList(photos: List<CosplayPhoto>) {
    if (photos.isEmpty()) {
        Text("No photos yet.")
    } else {
        LazyRow(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(photos) { photo ->
                AsyncImage(
                    model = photo.path,
                    contentDescription = "Cosplay photo",
                    modifier = Modifier.size(120.dp)
                )
            }
        }
    }
}