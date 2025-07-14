package com.example.conquest.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Serializable
object NewCosplayScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCosplayScreen(
    navController: NavController,
) {
    val cosplayViewModel: CosplayViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
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
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(bottom = 8.dp, top = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(32.dp)
                    ),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 18.dp, vertical = 4.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = if (inProgress) "In Progress" else "Planned",
                    )

                    Switch(
                        checked = inProgress,
                        onCheckedChange = { inProgress = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.secondary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedTrackColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }

            OutlinedTextField(
                value = characterName,
                onValueChange = { characterName = it },
                label = { Text("Character Name*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(32.dp)
            )

            OutlinedTextField(
                value = series,
                onValueChange = { series = it },
                label = { Text("Series*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(32.dp)
            )

            DatePickerFieldToModal(
                label = "Initial date*",
                selectedDate = initialDate,
                onDateSelected = { initialDate = it })

            DatePickerFieldToModal(
                label = "Due date", selectedDate = dueDate, onDateSelected = { dueDate = it })

            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it.filter { it.isDigit() || it == '.' } },
                label = { Text("Budget (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Text("$") },
                shape = RoundedCornerShape(32.dp)
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
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Text(
                    "Cancel", fontSize = 18.sp
                )
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
                            finished = false
                        )
                        cosplayViewModel.insertCosplay(newCosplay)
                        navController.popBackStack()
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Please fill out all required fields!"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Save", fontSize = 18.sp
                )
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 140.dp),
            snackbar = { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(32.dp),
                )
            })
    }
}
