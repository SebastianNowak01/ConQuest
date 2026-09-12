package com.maeldev.conquest.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Counterpart to [MyAddFab] for a screen that has no top app bar to go back from: the drawer
 * routes reach their list through the menu, these are reached from a button and have to offer
 * the way out themselves.
 */
@Composable
fun BoxScope.MyBackFab(onClick: () -> Unit) {
    MyFab(
        onClick = onClick,
        modifier = Modifier.align(Alignment.BottomCenter),
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.primary,
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Back",
    )
}
