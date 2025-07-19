package com.example.conquest.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class MainCosplayScreen(
    val uid: Int
)

@Composable
fun MainCosplayScreen(navBackStackEntry: NavBackStackEntry) {
    val args = navBackStackEntry.toRoute<MainCosplayScreen>()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Cosplay UID: ${args.uid}")
    }
}