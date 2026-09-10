package com.maeldev.conquest.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Guards a form's cancel action behind a confirmation once the form has been edited.
 *
 * Every form's cancel used to be a bare popBackStack, so a mistimed tap threw away everything
 * typed with no way back. Forms whose state is a data class get [isDirty] from comparing the
 * current state against the one they loaded.
 *
 * Returns the cancel handler the form should use in place of [onDiscard].
 */
@Composable
fun rememberDiscardChangesGuard(
    isDirty: Boolean,
    onDiscard: () -> Unit,
    title: String = "Discard changes?",
    message: String = "Your edits on this screen have not been saved.",
    confirmText: String = "Discard",
    dismissText: String = "Keep editing",
): () -> Unit {
    var showConfirmation by remember { mutableStateOf(false) }

    if (showConfirmation) {
        MyConfirmationDialog(
            title = title,
            message = message,
            confirmText = confirmText,
            dismissText = dismissText,
            onConfirm = {
                showConfirmation = false
                onDiscard()
            },
            onDismiss = { showConfirmation = false },
        )
    }

    return {
        if (isDirty) {
            showConfirmation = true
        } else {
            onDiscard()
        }
    }
}
