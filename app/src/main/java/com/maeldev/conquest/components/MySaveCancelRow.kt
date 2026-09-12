package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.maeldev.conquest.theme.UIConsts
import kotlinx.coroutines.launch

/** The accessibility labels and the "something is missing" message for a save/cancel row. */
data class SaveCancelLabels(
    val invalidMessage: String = "Please fill out all required fields!",
    val cancel: String = "Cancel",
    val save: String = "Save",
)

/**
 * What the validating [MySaveCancelRow] does on each outcome.
 *
 * [onInvalidAttempt] is what lets the form mark which fields are missing, since the snackbar
 * message cannot name them.
 */
data class SaveCancelActions(
    val onCancel: () -> Unit,
    val onCommit: () -> Unit,
    val postCommit: () -> Unit,
    val onInvalidAttempt: () -> Unit = {},
)

@Composable
fun BoxScope.MySaveCancelRow(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = UIConsts.paddingM,
    labels: SaveCancelLabels = SaveCancelLabels(),
) {
    Row(
        // MyFab applies the navigation bar inset itself, so the row does not repeat it.
        modifier =
            modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .padding(bottom = bottomPadding),
        horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingL),
    ) {
        // Cancel is deliberately not the error colour: red is reserved for the delete FAB in
        // selection mode, and leaving a form without saving is not destruction.
        MyFab(
            onClick = onCancel,
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.Close,
            contentDescription = labels.cancel,
        )

        MyFab(
            onClick = onSave,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.Check,
            contentDescription = labels.save,
        )
    }
}

@Composable
fun BoxScope.MySaveCancelRow(
    snackbarHostState: SnackbarHostState,
    isValid: Boolean,
    actions: SaveCancelActions,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = UIConsts.paddingM,
    labels: SaveCancelLabels = SaveCancelLabels(),
) {
    val coroutineScope = rememberCoroutineScope()

    MySaveCancelRow(
        modifier = modifier,
        bottomPadding = bottomPadding,
        onCancel = actions.onCancel,
        onSave = {
            if (!isValid) {
                actions.onInvalidAttempt()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(labels.invalidMessage)
                }
                return@MySaveCancelRow
            }
            actions.onCommit()
            actions.postCommit()
        },
        labels = labels,
    )
}
