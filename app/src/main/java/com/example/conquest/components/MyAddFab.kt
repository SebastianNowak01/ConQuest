package com.example.conquest.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun <T : Any> BoxScope.MyAddFab(navController: NavController, route: T) {
    MyFab(
        onClick = { navController.navigate(route) },
        modifier = Modifier.align(Alignment.BottomCenter),
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.primary,
        icon = Icons.Default.Add,
        contentDescription = "Add"
    )
}