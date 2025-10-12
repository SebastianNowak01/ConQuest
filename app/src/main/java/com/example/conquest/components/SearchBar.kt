package com.example.conquest.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchBar(
    value: String, onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Search", fontSize = 16.sp) },
        modifier = Modifier.padding(start = 16.dp, top = 6.dp, bottom = 6.dp, end = 16.dp),
        shape = RoundedCornerShape(32.dp),
        singleLine = true,
        colors = topAppBarTextFieldColorsObject(),
        textStyle = TextStyle(fontSize = 16.sp)
    )
}
