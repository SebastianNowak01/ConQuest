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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage

@Composable
fun MyImageBox(
    modifier: Modifier = Modifier,
    photoPath: String,
    contentDescription: String,
    size: Dp,
    shape: Shape = CircleShape,
    clickable: Boolean,
    onClick: () -> Unit,
) {
    val clickModifier = if (clickable) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
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
                contentDescription = "Pick image",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return
        }
        AsyncImage(
            model = photoPath,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape),
        )
    }
}

