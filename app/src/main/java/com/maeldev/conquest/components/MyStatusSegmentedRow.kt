package com.maeldev.conquest.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maeldev.conquest.data.classes.CosplayStatus

/**
 * Picks a cosplay's [CosplayStatus].
 *
 * All three states are on screen at once, so the control shows what the alternatives are and
 * which one is set. The switch this replaced could only say two of the three, and relabelled
 * itself as it was toggled — the label was the value rather than the question being asked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyStatusSegmentedRow(
    selected: CosplayStatus,
    onStatusChange: (CosplayStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        CosplayStatus.entries.forEachIndexed { index, status ->
            SegmentedButton(
                selected = status == selected,
                onClick = { onStatusChange(status) },
                shape =
                    SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = CosplayStatus.entries.size,
                    ),
                colors =
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.secondary,
                        activeContentColor = MaterialTheme.colorScheme.primary,
                        inactiveContainerColor = MaterialTheme.colorScheme.background,
                        inactiveContentColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Text(text = status.label)
            }
        }
    }
}
