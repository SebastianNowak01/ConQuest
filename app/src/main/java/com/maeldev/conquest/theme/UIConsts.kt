package com.maeldev.conquest.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object UIConsts {
    val fontSizeMedium = 16.sp

    val paddingXS = 4.dp
    val paddingS = 8.dp
    val paddingM = 16.dp
    val paddingL = 24.dp

    val cornerRadiusM = 16.dp
    val cornerRadiusL = 32.dp

    val strokeThin = 1.dp
    val elevationS = 4.dp

    val heightM = 120.dp
    val photoThumbSize = 120.dp
    val imageSizeS = 48.dp
    val imageSizeM = 80.dp
    val imageSizeL = 120.dp
    val imageBadgeSize = 28.dp
    val previewBlurRadius = 24.dp
    val previewImagePadding = 24.dp
    const val previewScrimAlpha = 0.35f
    const val decorativeBackgroundAlphaLight = 0.2f
    const val decorativeBackgroundAlphaDark = 0.2f

    val placeholderHeightL = 200.dp

    /**
     * Room reserved at the bottom of a form or list for the floating action buttons over it:
     * the 56dp FAB plus the row's own padding and the navigation bar inset, with room to spare
     * so the last field is not left sitting under the Save button.
     */
    val formBottomInset = 144.dp
    val listBottomInset = 144.dp

    val spacingS = 12.dp
    val spacingM = 18.dp
    val spacingL = 24.dp

    val letterSpacingS = 1.5.sp

    const val columnWidthFraction = 0.9f

    /** Clears the Save/Cancel FAB row so a snackbar sits just above it, not over the form. */
    val snackbarHostVerticalOffset = 88.dp
    const val fabCornerRadius = 50

    val searchBarVerticalPadding = 6.dp
    val columnVerticalSpacing = 20.dp
}
