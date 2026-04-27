package com.example.conquest.components

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.conquest.ui.theme.UIConsts

data class MyPhotoGridItem(
    val id: Int,
    val path: String,
)

@Composable
fun MyPhotoGrid(
    modifier: Modifier = Modifier,
    photos: List<MyPhotoGridItem>,
    selectedIds: Set<Int>,
    columns: GridCells,
    contentPadding: PaddingValues = PaddingValues(),
    emptyText: String,
    emptyModifier: Modifier = Modifier
        .fillMaxWidth()
        .height(UIConsts.placeholderHeightL),
    contentDescription: String,
    onItemClick: (MyPhotoGridItem) -> Unit,
    onItemLongClick: (MyPhotoGridItem) -> Unit,
) {
    val context = LocalContext.current

    if (photos.isEmpty()) {
        Box(
            modifier = emptyModifier,
            contentAlignment = Alignment.Center,
        ) {
            Text(emptyText)
        }
        return
    }

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
                val resolvedPhotoPath = resolveStoredImagePath(context, photo.path)
                AsyncImage(
                    model = resolvedPhotoPath,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

