package com.example.conquest.components

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.File
import java.io.InputStream
import java.io.OutputStream

const val IMAGE_STORAGE_DIR = "images"

/**
 * Returns the file extension inferred from the [Uri] MIME type, or null if unknown.
 */
fun getFileExtension(context: Context, uri: Uri): String? {
    val contentResolver: ContentResolver = context.contentResolver
    val mimeTypeMap = MimeTypeMap.getSingleton()
    val mimeType = contentResolver.getType(uri)
    return mimeTypeMap.getExtensionFromMimeType(mimeType)
}

fun buildManagedImageRelativePath(
    fileNamePrefix: String,
    extension: String,
    timestamp: Long = System.currentTimeMillis(),
): String {
    return "$IMAGE_STORAGE_DIR/${fileNamePrefix}_${timestamp}.$extension"
}

private fun getManagedImageFile(context: Context, storedPath: String): File {
    return File(context.filesDir, storedPath)
}

fun resolveStoredImagePath(context: Context, storedPath: String): String {
    val normalizedPath = storedPath.trim()
    if (normalizedPath.isEmpty()) {
        return ""
    }
    return getManagedImageFile(context, normalizedPath).absolutePath
}

fun deleteStoredImageByPath(context: Context, storedPath: String): Boolean {
    val normalizedPath = storedPath.trim()
    if (normalizedPath.isEmpty()) {
        return true
    }
    return getManagedImageFile(context, normalizedPath).delete()
}

/**
 * Copies the content addressed by [uri] into app internal storage ([Context.filesDir]) as [storedPath].
 *
 * @return relative stored path of the saved file.
 */
fun copyUriToInternalStorage(context: Context, uri: Uri, storedPath: String): String {
    val inputStream: InputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Unable to open input stream for uri=$uri")

    val file = getManagedImageFile(context, storedPath)
    file.parentFile?.mkdirs()
    val outputStream: OutputStream = file.outputStream()

    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }

    return storedPath
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
        val storedPath = buildManagedImageRelativePath(fileNamePrefix = fileNamePrefix, extension = extension)
        copyUriToInternalStorage(context, uri, storedPath)
    }
}
fun saveBitmapToInternalStorage(
    context: Context,
    bitmap: Bitmap,
    fileNamePrefix: String,
): Result<String> {
    return runCatching {
        val storedPath = buildManagedImageRelativePath(fileNamePrefix = fileNamePrefix, extension = "jpg")
        val file = getManagedImageFile(context, storedPath)
        file.parentFile?.mkdirs()
        file.outputStream().use { output ->
            val compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 95, output)
            if (!compressed) {
                error("Failed to compress bitmap")
            }
        }
        storedPath
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

fun interface ImagePickAndSaveLauncher {
    fun launch()
}




