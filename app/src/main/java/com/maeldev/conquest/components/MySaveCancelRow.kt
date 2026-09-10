package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.BoxScope
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

@Composable
fun BoxScope.MySaveCancelRow(
    modifier: Modifier = Modifier,
    bottomPadding: Dp = UIConsts.paddingM,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    cancelContentDescription: String = "Cancel",
    saveContentDescription: String = "Save",
) {
    Row(
        // MyFab applies the navigation bar inset itself, so the row does not repeat it.
        modifier =
            modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .padding(bottom = bottomPadding),
        horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingL)
    ) {
        // Cancel is deliberately not the error colour: the delete action in selection mode is a
        // red FAB with this same Close icon, and leaving a form without saving is not destruction.
        MyFab(
            onClick = onCancel,
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.Close,
            contentDescription = cancelContentDescription,
        )

        MyFab(
            onClick = onSave,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.Check,
            contentDescription = saveContentDescription,
        )
    }
}

@Composable
fun BoxScope.MySaveCancelRow(
    modifier: Modifier = Modifier,
    bottomPadding: Dp = UIConsts.paddingM,
    snackbarHostState: SnackbarHostState,
    isValid: Boolean,
    invalidMessage: String = "Please fill out all required fields!",
    onInvalidAttempt: () -> Unit = {},
    onCancel: () -> Unit,
    onCommit: () -> Unit,
    postCommit: () -> Unit,
    cancelContentDescription: String = "Cancel",
    saveContentDescription: String = "Save",
) {
    val coroutineScope = rememberCoroutineScope()

    MySaveCancelRow(
        modifier = modifier,
        bottomPadding = bottomPadding,
        onCancel = onCancel,
        onSave = {
            if (!isValid) {
                // The snackbar says something is missing; onInvalidAttempt is what lets the
                // form mark which fields, since the message cannot name them.
                onInvalidAttempt()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(invalidMessage)
                }
                return@MySaveCancelRow
            }
            onCommit()
            postCommit()
        },
        cancelContentDescription = cancelContentDescription,
        saveContentDescription = saveContentDescription,
    )
}

