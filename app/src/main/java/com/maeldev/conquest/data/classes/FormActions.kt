package com.maeldev.conquest.data.classes

import androidx.compose.material3.SnackbarHostState

/**
 * The save/cancel plumbing every form screen shares.
 *
 * Each `*FormContent` composable used to take these five as separate parameters, which made the
 * signatures long and repeated the same block at every call site. Bundling them keeps the form
 * composables focused on the fields they actually render.
 *
 * @param isDirty whether the user has changed anything, so cancelling can ask before discarding.
 */
data class FormActions(
    val snackbarHostState: SnackbarHostState,
    val onCancel: () -> Unit,
    val onCommit: () -> Unit,
    val postCommit: () -> Unit,
    val isDirty: Boolean = false,
)
