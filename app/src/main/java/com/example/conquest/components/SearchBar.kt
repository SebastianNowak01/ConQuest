package com.example.conquest.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    value: String, onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Search") },
        modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 16.dp),
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        colors = topAppBarTextFieldColorsObject()
    )
}
