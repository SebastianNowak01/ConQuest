package com.example.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.conquest.ui.theme.UIConsts
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
        modifier = modifier
            .align(androidx.compose.ui.Alignment.BottomCenter)
            .padding(bottom = bottomPadding)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingL)
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

@Composable
fun BoxScope.MySaveCancelRow(
    modifier: Modifier = Modifier,
    bottomPadding: Dp = UIConsts.paddingM,
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
        bottomPadding = bottomPadding,
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

