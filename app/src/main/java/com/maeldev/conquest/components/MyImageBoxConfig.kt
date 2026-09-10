package com.maeldev.conquest.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

/**
 * How a [MyImageBox] is sized, shaped and described.
 *
 * Set [showEditBadge] on a form: without it the control is an unlabelled grey shape that gives
 * no sign it can be tapped.
 */
data class MyImageBoxConfig(
    val size: Dp,
    val shape: Shape = CircleShape,
    val contentDescription: String? = null,
    val emptyContentDescription: String = "Pick image",
    val previewWhenPhotoExists: Boolean = false,
    val showEditBadge: Boolean = false,
)
