package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.maeldev.conquest.theme.UIConsts

@Composable
private fun MyExitSelectionFabButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MyFab(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.primary,
        icon = Icons.Default.Close,
        contentDescription = "Exit selection",
    )
}

@Composable
private fun MyExportSelectionFabButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MyFab(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        icon = Icons.Default.FileDownload,
        contentDescription = "Export",
    )
}

@Composable
private fun MySelectAllFabButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MyFab(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.primary,
        icon = Icons.Default.DoneAll,
        contentDescription = "Select all",
    )
}

/** The [MySelectionModeFabs] row, with export in place of delete. */
@Composable
fun BoxScope.MyExportSelectionModeFabs(
    selection: SelectionState,
    onExportSelection: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .align(Alignment.BottomCenter)
                .zIndex(2f),
        horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MyExitSelectionFabButton(onClick = { selection.clear() })
        MyExportSelectionFabButton(onClick = onExportSelection)
        MySelectAllFabButton(onClick = { selection.selectAll() })
    }
}
