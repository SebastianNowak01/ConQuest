package com.example.conquest.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.example.conquest.ui.theme.UIConsts

@Composable
fun SearchBar(
    value: String, onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Search", fontSize = UIConsts.fontSizeMedium) },
        modifier = Modifier.padding(
            start = UIConsts.paddingM,
            end = UIConsts.paddingM,
            top = UIConsts.searchBarVerticalPadding,
            bottom = UIConsts.searchBarVerticalPadding,
        ),
        shape = RoundedCornerShape(UIConsts.cornerRadiusL),
        singleLine = true,
        colors = topAppBarTextFieldColorsObject(),
        textStyle = TextStyle(fontSize = UIConsts.fontSizeMedium)
    )
}
