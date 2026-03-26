package com.example.conquest.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyHeaderText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(bottom = 8.dp, top = 8.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}