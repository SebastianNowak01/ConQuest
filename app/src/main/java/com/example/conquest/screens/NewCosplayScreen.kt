package com.example.conquest.screens

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass

@Serializable
object NewCosplayScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCosplayScreen(
    onSave: (CosplayProject) -> Unit,
    onCancel: () -> Unit
) {
    var characterName by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var initialDate by remember { mutableStateOf<Date?>(getCurrentDate()) }
    var dueDate by remember { mutableStateOf<Date?>(getCurrentDate()) }
    var budget by remember { mutableStateOf("") }
    var isInProgress by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "New Cosplay Project",
            style = MaterialTheme.typography.headlineMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isInProgress) "In Progress" else "Planned",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isInProgress,
                onCheckedChange = { isInProgress = it }
            )
        }

        OutlinedTextField(
            value = characterName,
            onValueChange = { characterName = it },
            label = { Text("Character Name*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = series,
            onValueChange = { series = it },
            label = { Text("Series*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        DatePickerFieldToModal(
            label = "Initial date*",
            selectedDate = initialDate,
            onDateSelected = { initialDate = it }
        )

        DatePickerFieldToModal(
            label = "Due date",
            selectedDate = dueDate,
            onDateSelected = { dueDate = it }
        )

        OutlinedTextField(
            value = budget,
            onValueChange = { budget = it.filter { it.isDigit() || it == '.' } },
            label = { Text("Budget (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            leadingIcon = { Text("$") }
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (characterName.isNotBlank() && series.isNotBlank() && initialDate != null) {
                        val cosplayProject = CosplayProject(
                            characterName = characterName,
                            series = series,
                            initialDate = initialDate!!,
                            dueDate = dueDate,
                            budget = budget.takeIf { it.isNotBlank() }?.toDoubleOrNull(),
                            isInProgress = isInProgress
                        )
                        onSave(cosplayProject)
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = characterName.isNotBlank() && series.isNotBlank() && initialDate != null
            ) {
                Text("Save")
            }
        }
    }
}

data class CosplayProject(
    val characterName: String,
    val series: String,
    val initialDate: Date,
    val dueDate: Date? = null,
    val budget: Double? = null,
    val isInProgress: Boolean = true
)

private fun getCurrentDate(): Date {
    return Calendar.getInstance().time
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Date?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedMillis = datePickerState.selectedDateMillis
                onDateSelected(selectedMillis?.let { Date(it) })
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun DatePickerFieldToModal(
    label: String,
    selectedDate: Date?,
    onDateSelected: (Date?) -> Unit
) {
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate?.let { convertDateToString(it) } ?: "",
        onValueChange = { },
        label = { Text(label) },
        placeholder = { Text("DD/MM/YYYY") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Select date")
        },
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )

    if (showModal) {
        DatePickerModal(
            onDateSelected = {
                onDateSelected(it)
                showModal = false
            },
            onDismiss = { showModal = false }
        )
    }
}

fun convertDateToString(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(date)
}