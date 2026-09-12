package com.maeldev.conquest.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.maeldev.conquest.theme.UIConsts

val menuShape: Shape = RoundedCornerShape(UIConsts.cornerRadiusM)

val menuContainerColor: Color
    @Composable get() = MaterialTheme.colorScheme.background

val menuBorder: BorderStroke
    @Composable get() = BorderStroke(UIConsts.strokeThin, MaterialTheme.colorScheme.outline)

@Composable
fun MyDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        shape = menuShape,
        containerColor = menuContainerColor,
        border = menuBorder,
        content = content,
    )
}
