package com.maeldev.conquest.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.zIndex
import com.maeldev.conquest.theme.UIConsts

private const val OVERLAY_Z_INDEX = 2f

@Composable
fun BoxScope.MyLoadingOverlay(label: String) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier =
            Modifier
                .matchParentSize()
                .zIndex(OVERLAY_Z_INDEX)
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = UIConsts.PREVIEW_SCRIM_ALPHA),
                )
                .clickable(interactionSource = interactionSource, indication = null) {},
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier =
                Modifier
                    .size(UIConsts.loadingIndicatorSize)
                    .semantics { contentDescription = label },
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
