package com.maeldev.conquest.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import com.maeldev.conquest.theme.UIConsts

private const val SINGLE_LINE_MAX_LINES = 1
private const val MULTI_LINE_MAX_LINES = 6

/**
 * How an input field behaves and is shaped.
 *
 * These travel together far more often than they vary independently, and keeping them out of
 * [MyInputField]'s own parameter list leaves it stating just the value, label and validation.
 */
data class InputFieldOptions(
    val singleLine: Boolean = true,
    /** Defaults to 1 for single-line fields and 6 otherwise. */
    val maxLines: Int? = null,
    val height: Dp? = null,
    val shape: RoundedCornerShape = RoundedCornerShape(UIConsts.inputCornerRadius),
    val keyboardType: KeyboardType? = null,
    val filterDecimal: Boolean = false,
) {
    val resolvedMaxLines: Int
        get() = maxLines ?: if (singleLine) SINGLE_LINE_MAX_LINES else MULTI_LINE_MAX_LINES

    val resolvedKeyboardType: KeyboardType
        get() = keyboardType ?: if (filterDecimal) KeyboardType.Decimal else KeyboardType.Text
}

/** Validation feedback for a field, shown once a save has been attempted. */
data class FieldError(
    val isError: Boolean = false,
    val message: String? = null,
)

/**
 * App-wide input field wrapper.
 *
 * Supports:
 * - default single-line text fields (e.g., Name)
 * - numeric/decimal fields with optional filtering (e.g., Cost)
 * - multi-line "notes" fields with a fixed height and max lines
 * - required fields that mark themselves once a save has been attempted ([FieldError])
 */
@Composable
fun MyInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    options: InputFieldOptions = InputFieldOptions(),
    error: FieldError = FieldError(),
    onClear: (() -> Unit)? = null,
) {
    val appliedModifier =
        modifier
            .fillMaxWidth()
            .let { m -> if (options.height != null) m.height(options.height) else m }

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            val filtered =
                if (options.filterDecimal) {
                    newValue.filter { c -> c.isDigit() || c == '.' }
                } else {
                    newValue
                }
            onValueChange(filtered)
        },
        label = { Text(label) },
        modifier = appliedModifier,
        shape = options.shape,
        singleLine = options.singleLine,
        maxLines = options.resolvedMaxLines,
        isError = error.isError,
        supportingText = errorSupportingText(error.isError, error.message),
        trailingIcon = clearTrailingIcon(value.isNotEmpty(), label, onClear),
        keyboardOptions = KeyboardOptions(keyboardType = options.resolvedKeyboardType),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                errorContainerColor = MaterialTheme.colorScheme.background,
            ),
    )
}
