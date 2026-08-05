package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.maeldev.conquest.theme.UIConsts

@Composable
private fun MyExitSelectionFabButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    MyFab(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.primary,
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Exit selection",
    )
}

@Composable
private fun MyExportSelectionFabButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    MyFab(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary,
        icon = Icons.Default.Share,
        contentDescription = "Export",
    )
}

@Composable
private fun MySelectAllFabButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    MyFab(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.primary,
        icon = Icons.Default.DoneAll,
        contentDescription = "Select all",
    )
}

@Composable
fun BoxScope.MyExportSelectionModeFabs(
    onExitSelection: () -> Unit,
    onExportSelection: () -> Unit,
    onSelectAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .zIndex(2f),
        horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MyExitSelectionFabButton(onClick = onExitSelection)
        MyExportSelectionFabButton(onClick = onExportSelection)
        MySelectAllFabButton(onClick = onSelectAll)
    }
}
