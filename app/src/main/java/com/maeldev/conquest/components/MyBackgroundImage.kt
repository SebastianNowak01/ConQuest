package com.maeldev.conquest.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import com.maeldev.conquest.R
import com.maeldev.conquest.theme.UIConsts

@Composable
fun MyBackgroundImage(
    imageResId: Int = R.drawable.ic_launcher_foreground,
    contentDescription: String? = null,
) {
    val backgroundAlpha = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) {
        UIConsts.decorativeBackgroundAlphaDark
    } else {
        UIConsts.decorativeBackgroundAlphaLight
    }
    val density = LocalDensity.current
    val windowContainerSize = LocalWindowInfo.current.containerSize
    val screenWidth = with(density) { windowContainerSize.width.toDp() }
    val screenHeight = with(density) { windowContainerSize.height.toDp() }
    Box(modifier =  Modifier.background(MaterialTheme.colorScheme.background)) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = contentDescription,
            modifier = Modifier
                .align(Alignment.Center)
                .size(
                    width = screenWidth,
                    height = screenHeight,
                ),
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            alpha = backgroundAlpha,
        )
    }
}


