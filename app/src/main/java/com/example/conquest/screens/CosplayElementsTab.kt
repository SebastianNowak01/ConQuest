package com.example.conquest.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.conquest.components.MyFab


@Composable
fun CosplayElementsTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Cosplay Elements Content")
        MyFab(
            onClick = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}