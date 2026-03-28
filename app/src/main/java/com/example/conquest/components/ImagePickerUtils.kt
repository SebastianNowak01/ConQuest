package com.example.conquest.components

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Returns the file extension inferred from the [Uri] MIME type, or null if unknown.
 */
fun getFileExtension(context: Context, uri: Uri): String? {
    val contentResolver: ContentResolver = context.contentResolver
    val mimeTypeMap = MimeTypeMap.getSingleton()
    val mimeType = contentResolver.getType(uri)
    return mimeTypeMap.getExtensionFromMimeType(mimeType)
}

/**
 * Copies the content addressed by [uri] into app internal storage ([Context.filesDir]) as [fileName].
 *
 * @return absolute path of the saved file.
 */
fun copyUriToInternalStorage(context: Context, uri: Uri, fileName: String): String {
    val inputStream: InputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Unable to open input stream for uri=$uri")

    val file = File(context.filesDir, fileName)
    val outputStream: OutputStream = file.outputStream()

    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }

    return file.absolutePath
}

/**
 * Saves a picked image [Uri] into internal storage using a generated file name.
 *
 * @param fileNamePrefix prefix for the generated file name (e.g., "cosplay_photo").
 * @param fallbackExtension used when the extension cannot be inferred from the uri MIME type.
 */
fun saveImageUriToInternalStorage(
    context: Context,
    uri: Uri,
    fileNamePrefix: String,
    fallbackExtension: String = "jpg",
): Result<String> {
    return runCatching {
        val extension = getFileExtension(context, uri) ?: fallbackExtension
        val fileName = "${fileNamePrefix}_${System.currentTimeMillis()}.$extension"
        copyUriToInternalStorage(context, uri, fileName)
    }
}

@Composable
fun pickAndSaveImageLauncher(
    context: Context,
    fileNamePrefix: String,
    onSaved: (String) -> Unit,
    onError: (Throwable) -> Unit = {},
): ImagePickAndSaveLauncher {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult

        saveImageUriToInternalStorage(
            context = context,
            uri = uri,
            fileNamePrefix = fileNamePrefix,
        ).onSuccess(onSaved).onFailure(onError)
    }

    return remember(launcher) {
        ImagePickAndSaveLauncher { launcher.launch("image/*") }
    }
}

// Intentionally no UpperCamelCase wrapper: Compose lint requires lowerCamelCase for
// composable functions that return a value.

fun interface ImagePickAndSaveLauncher {
    fun launch()
}




