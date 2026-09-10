package com.maeldev.conquest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.maeldev.conquest.theme.UIConsts

/**
 * Scrolling body of a create/edit form.
 *
 * Forms share a Box with the Save/Cancel FAB row, so the column scrolls (a long form does not
 * fit a short screen), reserves [UIConsts.formBottomInset] so the last field is not stranded
 * under those FABs, and follows the keyboard instead of being covered by it.
 */
@Composable
fun BoxScope.MyColumn(content: @Composable () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth(UIConsts.columnWidthFraction)
            .verticalScroll(scrollState)
            .imePadding()
            .padding(
                start = UIConsts.paddingM,
                end = UIConsts.paddingM,
                bottom = UIConsts.formBottomInset,
            ),
        verticalArrangement = Arrangement.spacedBy(UIConsts.columnVerticalSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}
