package com.maeldev.conquest.components

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.maeldev.conquest.theme.UIConsts

data class MyPhotoGridItem(
    val id: Int,
    val path: String,
    /** Marks the thumbnail, so a photo carrying a note is visible without opening it. */
    val hasNote: Boolean = false,
)

@Composable
fun MyPhotoGrid(
    modifier: Modifier = Modifier,
    photos: List<MyPhotoGridItem>,
    selectedIds: Set<Int>,
    columns: GridCells,
    contentPadding: PaddingValues = PaddingValues(),
    contentDescription: String,
    onItemClick: (MyPhotoGridItem) -> Unit,
    onItemLongClick: (MyPhotoGridItem) -> Unit,
) {
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = columns,
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(UIConsts.paddingS),
        horizontalArrangement = Arrangement.spacedBy(UIConsts.paddingS),
    ) {
        items(items = photos, key = { it.id }) { photo ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(
                        width = UIConsts.strokeThin,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(UIConsts.cornerRadiusM),
                    )
                    .combinedClickable(
                        onClick = { onItemClick(photo) },
                        onLongClick = { onItemLongClick(photo) },
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedIds.contains(photo.id)) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.background
                    },
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = UIConsts.elevationS),
                shape = RoundedCornerShape(UIConsts.cornerRadiusM),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val resolvedPhotoPath = resolveStoredImagePath(context, photo.path)
                    AsyncImage(
                        model = resolvedPhotoPath,
                        contentDescription = contentDescription,
                        // Cells are square but photos are not, so crop rather than distort.
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )

                    if (photo.hasNote) {
                        Surface(
                            modifier =
                                Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(UIConsts.paddingXS),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.primary,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Notes,
                                contentDescription = "Has a note",
                                modifier =
                                    Modifier
                                        .size(UIConsts.imageBadgeSize)
                                        .padding(UIConsts.paddingXS),
                            )
                        }
                    }
                }
            }
        }
    }
}

