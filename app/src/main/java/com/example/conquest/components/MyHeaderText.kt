package com.example.conquest.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.conquest.ui.theme.UIConsts

@Composable
fun MyHeaderText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = UIConsts.letterSpacingS,
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(top = UIConsts.paddingS, bottom = UIConsts.paddingS)
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}