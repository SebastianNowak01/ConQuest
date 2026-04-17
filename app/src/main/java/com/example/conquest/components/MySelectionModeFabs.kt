package com.example.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.example.conquest.ui.theme.UIConsts

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
private fun MyDeleteSelectionFabButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    MyFab(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.primary,
        icon = Icons.Default.Close,
        contentDescription = "Delete",
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
fun BoxScope.MyExitSelectionFab(onClick: () -> Unit) {
    MyExitSelectionFabButton(
        onClick = onClick,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .zIndex(2f),
    )
}

@Composable
fun BoxScope.MySelectAllFab(onClick: () -> Unit) {
    MySelectAllFabButton(
        onClick = onClick,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .zIndex(2f),
    )
}

@Composable
fun BoxScope.MySelectionModeFabs(
    onExitSelection: () -> Unit,
    onDeleteSelection: () -> Unit,
    onSelectAll: () -> Unit,
) {
    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .zIndex(2f),
        horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MyExitSelectionFabButton(onClick = onExitSelection)
        MyDeleteSelectionFabButton(onClick = onDeleteSelection)
        MySelectAllFabButton(onClick = onSelectAll)
    }
}
