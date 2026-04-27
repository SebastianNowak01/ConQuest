package com.example.conquest.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.conquest.ui.theme.UIConsts

@Composable
fun MyCosplayRow(
    name: String,
    series: String,
    photoPath: String,
) {
    Row {
        MyImageBox(
            photoPath = photoPath,
            contentDescription = name,
            size = UIConsts.imageSizeS,
            clickable = false,
            onClick = {},
            emptyContentDescription = "Cosplay photo",
        )
        Spacer(modifier = Modifier.width(UIConsts.paddingS))
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = series,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

