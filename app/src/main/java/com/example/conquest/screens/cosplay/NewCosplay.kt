package com.example.conquest.screens.cosplay

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import com.example.conquest.components.DatePickerFieldToModal
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MySnackbarHost
import com.example.conquest.data.classes.CosplayFormState

@Serializable
object NewCosplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCosplay(
    navController: NavController,
) {
    val cosplayViewModel: CosplayViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    var form by remember { mutableStateOf(CosplayFormState()) }

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "New Project")

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
                        text = if (form.inProgress) "In Progress" else "Planned",
                    )

                    Switch(
                        checked = form.inProgress,
                        onCheckedChange = { form = form.copy(inProgress = it) },
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
                value = form.characterName,
                onValueChange = { form = form.copy(characterName = it) },
                label = { Text("Character Name*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(32.dp)
            )

            OutlinedTextField(
                value = form.series,
                onValueChange = { form = form.copy(series = it) },
                label = { Text("Series*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(32.dp)
            )

            DatePickerFieldToModal(
                label = "Initial date*",
                selectedDate = form.initialDate,
                onDateSelected = { form = form.copy(initialDate = it) })

            DatePickerFieldToModal(
                label = "Due date",
                selectedDate = form.dueDate,
                onDateSelected = { form = form.copy(dueDate = it) })

            OutlinedTextField(
                value = form.budget,
                onValueChange = {
                    form = form.copy(budget = it.filter { it.isDigit() || it == '.' })
                },
                label = { Text("Budget (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Text("$") },
                shape = RoundedCornerShape(32.dp)
            )

        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onCancel = { navController.popBackStack() },
            onCommit = { cosplayViewModel.insertCosplay(form.toEntity(uid = 0, finished = false)) },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
