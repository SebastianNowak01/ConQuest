package com.example.conquest.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex

@Composable
fun BoxScope.MyDeleteFab(onClick: () -> Unit) {
    MyFab(
        onClick = onClick,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .zIndex(2f),
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.primary,
        icon = Icons.Default.Close,
        contentDescription = "Delete"
    )
}