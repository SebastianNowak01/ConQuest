package com.maeldev.conquest.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.maeldev.conquest.theme.UIConsts

/**
 * How many items are selected, while selection mode is active.
 *
 * The count previously appeared only inside the delete confirmation dialog, so up to that point
 * the only sign of how much was selected was counting the tinted rows.
 */
@Composable
fun BoxScope.MySelectionCountLabel(
    selection: SelectionState,
    itemLabelSingular: String,
    itemLabelPlural: String = "${itemLabelSingular}s",
) {
    if (!selection.isActive) {
        return
    }

    val label = if (selection.count == 1) itemLabelSingular else itemLabelPlural

    Surface(
        modifier =
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = UIConsts.paddingS)
                .zIndex(2f),
        shape = RoundedCornerShape(UIConsts.cornerRadiusL),
        color = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.primary,
        shadowElevation = UIConsts.elevationS,
    ) {
        Text(
            text = "${selection.count} $label selected",
            style = MaterialTheme.typography.labelLarge,
            modifier =
                Modifier.padding(
                    horizontal = UIConsts.paddingM,
                    vertical = UIConsts.paddingS,
                ),
        )
    }
}
