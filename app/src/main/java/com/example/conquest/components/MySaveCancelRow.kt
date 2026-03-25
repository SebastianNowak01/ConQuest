package com.example.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Reusable save/cancel FAB row.
 *
 * Prefer the callback-only overload for maximum reuse.
 */
@Composable
fun MySaveCancelRow(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    cancelContentDescription: String = "Cancel",
    saveContentDescription: String = "Save",
) {
    Row(
        modifier = modifier
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        MyFab(
            onClick = onCancel,
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.Close,
            contentDescription = cancelContentDescription
        )

        MyFab(
            onClick = onSave,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.Add,
            contentDescription = saveContentDescription
        )
    }
}

/**
 * Convenience overload for the common "validate -> snackbar -> commit -> postCommit" flow.
 */
@Composable
fun MySaveCancelRow(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    isValid: Boolean,
    invalidMessage: String = "Please fill out all required fields!",
    onCancel: () -> Unit,
    onCommit: () -> Unit,
    postCommit: () -> Unit,
    cancelContentDescription: String = "Cancel",
    saveContentDescription: String = "Save",
) {
    val coroutineScope = rememberCoroutineScope()

    MySaveCancelRow(
        modifier = modifier,
        onCancel = onCancel,
        onSave = {
            if (!isValid) {
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

