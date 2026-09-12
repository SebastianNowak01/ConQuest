package com.maeldev.conquest.components

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.maeldev.conquest.theme.UIConsts

/**
 * Card and list metrics for [MyLazyColumn]. Grouped so the list composable's signature stays
 * about *what* it shows rather than how it is spaced.
 */
data class MyListStyle(
    // The floating buttons sit over the end of the list, so leave room to scroll clear of them.
    val contentPadding: PaddingValues =
        PaddingValues(
            start = UIConsts.screenHorizontalPadding,
            end = UIConsts.screenHorizontalPadding,
            top = UIConsts.paddingM,
            bottom = UIConsts.listBottomInset,
        ),
    val spacedBy: Dp = UIConsts.spacingM,
    val cardCornerRadius: Dp = UIConsts.cornerRadiusL,
    val cardElevation: Dp = UIConsts.elevationS,
    val cardContentPadding: Dp = UIConsts.paddingM,
)

/** What a row in [MyLazyColumn] does when tapped, long-pressed, or asked whether it is selected. */
data class MyListItemActions<T>(
    val isSelected: (T) -> Boolean,
    val onClick: (T) -> Unit,
    val onLongClick: (T) -> Unit,
)

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
    items: List<T>,
    actions: MyListItemActions<T>,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    style: MyListStyle = MyListStyle(),
    content: @Composable ColumnScope.(T) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = style.contentPadding,
        verticalArrangement = Arrangement.spacedBy(style.spacedBy),
    ) {
        items(
            items = items,
            key = if (key != null) ({ item: T -> key(item) }) else null,
        ) { item ->
            MySelectableCardItem(
                selected = actions.isSelected(item),
                style = style,
                onClick = { actions.onClick(item) },
                onLongClick = { actions.onLongClick(item) },
                modifier = Modifier.animateItem(),
            ) {
                content(item)
            }
        }
    }
}

@Composable
private fun MySelectableCardItem(
    selected: Boolean,
    style: MyListStyle,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(style.cardCornerRadius)

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .border(UIConsts.strokeThin, MaterialTheme.colorScheme.outline, shape)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.background
                    },
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = style.cardElevation),
        shape = shape,
    ) {
        Column(
            modifier = Modifier.padding(style.cardContentPadding),
            content = content,
        )
    }
}
