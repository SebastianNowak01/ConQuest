package com.maeldev.conquest.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.maeldev.conquest.theme.UIConsts

/**
 * Image slot: an avatar in a list row, or the editable picture at the top of a form.
 *
 * On a form pass [showEditBadge] and [onClear]. Without them the control is an unlabelled grey
 * circle that gives no sign it can be tapped, and a picture, once chosen, cannot be taken back
 * off — only replaced.
 */
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
    showEditBadge: Boolean = false,
    onClear: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    var showPreview by remember(photoPath) { mutableStateOf(false) }
    val hasPhoto = photoPath.isNotEmpty()
    val resolvedPhotoPath = resolveStoredImagePath(context, photoPath)
    val clickModifier =
        when {
            !clickable -> Modifier
            previewWhenPhotoExists && hasPhoto -> Modifier.clickable { showPreview = true }
            else -> Modifier.clickable(onClick = onClick)
        }

    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .then(clickModifier),
            contentAlignment = Alignment.Center,
        ) {
            if (hasPhoto) {
                AsyncImage(
                    model = resolvedPhotoPath,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape),
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = emptyContentDescription,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (showEditBadge) {
            MyImageBoxBadge(
                icon = Icons.Default.Edit,
                contentDescription = if (hasPhoto) "Change image" else emptyContentDescription,
                onClick = onClick,
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }

        if (hasPhoto && onClear != null) {
            MyImageBoxBadge(
                icon = Icons.Default.Close,
                contentDescription = "Remove image",
                onClick = onClear,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }

    if (showPreview) {
        MyPhotoPreview(
            photoPath = photoPath,
            contentDescription = contentDescription,
            onDismiss = { showPreview = false },
        )
    }
}

@Composable
private fun MyImageBoxBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .size(UIConsts.imageBadgeSize)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.padding(UIConsts.paddingXS),
        )
    }
}
