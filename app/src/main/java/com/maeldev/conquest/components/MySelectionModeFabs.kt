package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun BoxScope.MySelectionModeFabs(
    selection: SelectionState,
    itemLabelSingular: String,
    itemLabelPlural: String,
    onDeleteSelection: (Set<Int>) -> Unit,
    deleteDialogConfirmText: String = "Delete",
    deleteDialogDismissText: String = "Cancel",
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val selectedCount = selection.count
    val itemLabel = if (selectedCount == 1) itemLabelSingular else itemLabelPlural

    fun dismissDeleteDialog() {
        showDeleteConfirmation = false
    }

    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .zIndex(2f),
        horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingM),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MyExitSelectionFabButton(onClick = { selection.clear() })
        MyDeleteSelectionFabButton(onClick = { showDeleteConfirmation = true })
        MySelectAllFabButton(onClick = { selection.selectAll() })
    }

    if (showDeleteConfirmation) {
        MyConfirmationDialog(
            title = "Delete selected $itemLabel?",
            message = "This will permanently delete $selectedCount selected $itemLabel.",
            confirmText = deleteDialogConfirmText,
            dismissText = deleteDialogDismissText,
            onConfirm = {
                dismissDeleteDialog()
                onDeleteSelection(selection.selectedIds)
                selection.clear()
            },
            onDismiss = {
                dismissDeleteDialog()
            },
        )
    }
}
