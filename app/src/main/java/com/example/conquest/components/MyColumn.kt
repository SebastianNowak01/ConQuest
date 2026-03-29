package com.example.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.conquest.ui.theme.UIConsts

@Composable
fun BoxScope.MyColumn(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth(UIConsts.columnWidthFraction)
            .padding(start = UIConsts.paddingM, end = UIConsts.paddingM),
        verticalArrangement = Arrangement.spacedBy(UIConsts.columnVerticalSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}