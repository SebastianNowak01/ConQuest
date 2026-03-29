package com.example.conquest.components

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.example.conquest.ui.theme.UIConsts

/**
 * A common pattern in this app is a padded, spaced LazyColumn that shows Cards which:
 * - are full width
 * - have rounded corners + border
 * - change container color when selected
 * - support selection mode (multi-select) and normal click
 *
 * This component centralizes that pattern, while still letting callers provide the inner content.
 */
@Composable
fun <T> MyLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    key: ((T) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues(UIConsts.paddingM),
    spacedBy: Dp = UIConsts.spacingM,
    cardCornerRadius: Dp = UIConsts.cornerRadiusL,
    cardElevation: Dp = UIConsts.elevationS,
    isSelected: (T) -> Boolean,
    onClick: (T) -> Unit,
    onLongClick: (T) -> Unit,
    cardContentPadding: Dp = UIConsts.paddingM,
    content: @Composable ColumnScope.(T) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(spacedBy),
    ) {
        items(
            items = items,
            key = if (key != null) ({ item: T -> key(item) }) else null,
        ) { item ->
            MySelectableCardItem(
                selected = isSelected(item),
                cornerRadius = cardCornerRadius,
                elevation = cardElevation,
                onClick = { onClick(item) },
                onLongClick = { onLongClick(item) },
                contentPadding = cardContentPadding,
            ) {
                content(item)
            }
        }
    }
}

@Composable
private fun MySelectableCardItem(
    selected: Boolean,
    cornerRadius: Dp,
    elevation: Dp,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    contentPadding: Dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(UIConsts.strokeThin, MaterialTheme.colorScheme.outline, shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.background,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = shape,
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}



