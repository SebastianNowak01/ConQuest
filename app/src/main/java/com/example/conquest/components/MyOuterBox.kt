package com.example.conquest.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.conquest.R
import com.example.conquest.ui.theme.UIConsts

@Composable
fun MyOuterBox(content: @Composable BoxScope.() -> Unit) {
    val backgroundAlpha = if (MaterialTheme.colorScheme.background.luminance() < 0.5f) {
        UIConsts.decorativeBackgroundAlphaDark
    } else {
        UIConsts.decorativeBackgroundAlphaLight
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            alpha = backgroundAlpha,
        )
        content()
    }
}
