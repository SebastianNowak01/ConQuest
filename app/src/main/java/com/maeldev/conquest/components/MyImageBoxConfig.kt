package com.maeldev.conquest.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.maeldev.conquest.theme.UIConsts

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
) {
    /**
     * Shape for the corner badges, following [shape] so they read as part of the image rather than
     * as circles stuck onto a soft-square.
     *
     * The box's own radius cannot simply be reused: a badge is only [UIConsts.imageBadgeSize]
     * across, so any radius at or above half of that clamps back to a full circle — the very thing
     * being fixed here. A rounded box therefore gets [UIConsts.cornerRadiusS], which at badge size
     * reads as the same softness the 120dp image has at [UIConsts.cornerRadiusM].
     */
    val badgeShape: Shape
        get() =
            if (shape == CircleShape) {
                CircleShape
            } else {
                RoundedCornerShape(UIConsts.cornerRadiusS)
            }
}
