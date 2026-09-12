package com.maeldev.conquest.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.maeldev.conquest.theme.UIConsts

@Composable
fun MyFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color,
    icon: ImageVector,
    contentDescription: String,
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(UIConsts.FAB_CORNER_RADIUS),
        // These FABs are all anchored to the bottom of the screen, so the inset that matters is
        // the navigation bar's. This used to apply statusBarsPadding instead, which reserved the
        // wrong inset at the wrong end and made the FAB row taller than it looked.
        modifier =
            modifier
                .navigationBarsPadding()
                .padding(bottom = UIConsts.paddingM),
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}
