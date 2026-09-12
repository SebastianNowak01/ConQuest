package com.maeldev.conquest.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.maeldev.conquest.theme.UIConsts

/**
 * An icon button in a top app bar. Every icon that sits in a bar goes through here, which is
 * what keeps them one size — sized individually at each call site they drifted apart.
 */
@Composable
fun MyIcon(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier.size(UIConsts.topAppBarIconSize),
        )
    }
}
