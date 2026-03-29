package com.example.conquest.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * App-wide input field wrapper.
 *
 * Supports:
 * - default single-line text fields (e.g., Name)
 * - numeric/decimal fields with optional filtering (e.g., Cost)
 * - multi-line "notes" fields with a fixed height and max lines
 */
@Composable
fun MyInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 6,
    height: Dp? = null,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    keyboardType: KeyboardType? = null,
    filterDecimal: Boolean = false,
) {
    val resolvedKeyboardType = keyboardType ?: if (filterDecimal) KeyboardType.Decimal else KeyboardType.Text

    val appliedModifier = modifier
        .fillMaxWidth()
        .let { m -> if (height != null) m.height(height) else m }

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            val filtered = if (filterDecimal) {
                newValue.filter { c -> c.isDigit() || c == '.' }
            } else {
                newValue
            }
            onValueChange(filtered)
        },
        label = { Text(label) },
        modifier = appliedModifier,
        shape = shape,
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = resolvedKeyboardType),
    )
}

