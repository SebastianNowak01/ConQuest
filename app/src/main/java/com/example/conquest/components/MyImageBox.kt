package com.example.conquest.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage

@Composable
fun MyImageBox(
    modifier: Modifier = Modifier,
    photoPath: String,
    contentDescription: String? = null,
    size: Dp,
    shape: Shape = CircleShape,
    clickable: Boolean,
    onClick: () -> Unit,
    emptyContentDescription: String = "Pick image",
    previewWhenPhotoExists: Boolean = false,
) {
    val context = LocalContext.current
    var showPreview by remember(photoPath) { mutableStateOf(false) }
    val hasPhoto = photoPath.isNotEmpty()
    val resolvedPhotoPath = resolveStoredImagePath(context, photoPath)

    val clickModifier = if (!clickable) {
        Modifier
    } else if (previewWhenPhotoExists && hasPhoto) {
        Modifier.clickable(onClick = { showPreview = true })
    } else {
        Modifier.clickable(onClick = onClick)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(clickModifier),
        contentAlignment = Alignment.Center,
    ) {
        if (photoPath.isEmpty()) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = emptyContentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return
        }
        AsyncImage(
            model = resolvedPhotoPath,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape),
        )
    }

    if (showPreview) {
        MyPhotoPreview(
            photoPath = photoPath,
            contentDescription = contentDescription,
            onDismiss = { showPreview = false },
        )
    }
}
