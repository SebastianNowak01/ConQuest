package com.maeldev.conquest.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * The trailing slot and supporting line shared by the app's text and date fields.
 *
 * Both are `(@Composable () -> Unit)?` arguments that are only sometimes present, which reads
 * badly inline; keeping them here means a field declares its intent in one line and the two
 * field types cannot drift apart in how an error or a clear button looks.
 */
@Composable
fun errorSupportingText(
    isError: Boolean,
    message: String?,
): (@Composable () -> Unit)? {
    if (!isError || message == null) {
        return null
    }
    return { Text(message) }
}

@Composable
fun clearTrailingIcon(
    show: Boolean,
    label: String,
    onClear: (() -> Unit)?,
): (@Composable () -> Unit)? {
    if (!show || onClear == null) {
        return null
    }
    return {
        IconButton(onClick = onClear) {
            Icon(Icons.Default.Clear, contentDescription = "Clear $label")
        }
    }
}

/**
 * Deletes a picked-but-unsaved image file, leaving the one already stored on the entity alone.
 *
 * Removing a picture the user just chose should not leave the file behind, but the path loaded
 * from the database is still the saved state and must survive a cancel.
 */
fun discardUnsavedImage(
    context: android.content.Context,
    currentPath: String,
    originalPath: String?,
) {
    val unsaved = currentPath.takeIf { it.isNotBlank() && it != originalPath } ?: return
    deleteStoredImageByPath(context, unsaved)
}
