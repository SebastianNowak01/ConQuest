package com.example.conquest.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import java.util.*
import com.example.conquest.components.DatePickerFieldToModal
import com.example.conquest.components.getCurrentDate
import com.example.conquest.data.entity.Cosplay

@Serializable
object NewCosplayScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCosplayScreen(
    navController: NavController,
) {
    val cosplayViewModel: CosplayViewModel = viewModel()
    var characterName by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var initialDate by remember { mutableStateOf<Date?>(getCurrentDate()) }
    var dueDate by remember { mutableStateOf<Date?>(getCurrentDate()) }
    var budget by remember { mutableStateOf("") }
    var inProgress by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.9f)
                .padding(top = 70.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    text = if (inProgress) "In Progress" else "Planned",
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = inProgress,
                    onCheckedChange = { inProgress = it }
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

        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {navController.popBackStack()},
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (characterName.isNotBlank() && series.isNotBlank() && initialDate != null) {
                        val newCosplay = Cosplay(
                            name = characterName,
                            series = series,
                            initialDate = initialDate!!,
                            dueDate = dueDate,
                            budget = budget.takeIf { it.isNotBlank() }?.toDoubleOrNull(),
                            inProgress = inProgress,
                            uid = 0,
                        )
                        cosplayViewModel.insertCosplay(newCosplay)
                        navController.popBackStack()
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
