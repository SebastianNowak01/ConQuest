package com.maeldev.conquest.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.maeldev.conquest.theme.UIConsts

/**
 * Reads out one boolean on a list row.
 *
 * These states used to be shown with the same control that edits them — a full-width switch with
 * its callback set to null, which looks operable and is not — or, on elements, with bare text
 * that vanished when false, so "not ready yet" was indistinguishable from a row that had never
 * said anything. A chip is legible in both states: filled with its icon when set, outlined and
 * muted when not.
 */
@Composable
fun MyStatusChip(
    label: String,
    icon: ImageVector,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val container = if (active) MaterialTheme.colorScheme.secondary else Color.Transparent
    val content =
        if (active) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    val border = if (active) null else BorderStroke(UIConsts.strokeThin, content)
    val stateDescription = if (active) label else "not $label"

    Surface(
        modifier = modifier.clearAndSetSemantics { contentDescription = stateDescription },
        shape = RoundedCornerShape(UIConsts.cornerRadiusL),
        color = container,
        contentColor = content,
        border = border,
    ) {
        Row(
            modifier =
                Modifier.padding(
                    horizontal = UIConsts.paddingS,
                    vertical = UIConsts.chipVerticalPadding,
                ),
            horizontalArrangement = Arrangement.spacedBy(UIConsts.paddingXS),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(UIConsts.chipIconSize),
            )
            Text(text = label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
