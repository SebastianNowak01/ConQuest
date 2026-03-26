package com.example.conquest.screens.cosplay

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.DatePickerFieldToModal
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MySnackbarHost
import com.example.conquest.data.classes.NewTaskFormState
import kotlinx.serialization.Serializable

@Serializable
data class NewTask(val cosplayId: Int)

@Composable
fun NewTask(
    cosplayId: Int,
    navController: NavController,
) {
    val cosplayViewModel: CosplayViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    var form by remember { mutableStateOf(NewTaskFormState()) }

    MyOuterBox {
        MyColumn {
            Text(
                text = "Add Task",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(bottom = 8.dp, top = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = form.taskName,
                onValueChange = { form = form.copy(taskName = it) },
                label = { Text("Task Name*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(32.dp)
            )

            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Done switch
                Card(
                    modifier = Modifier
                        .weight(1f)
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
                            .padding(horizontal = 18.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "Done")
                        Switch(
                            checked = form.done,
                            onCheckedChange = { form = form.copy(done = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                uncheckedTrackColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
                // Alarm switch
                Card(
                    modifier = Modifier
                        .weight(1f)
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
                            .padding(horizontal = 18.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "Alarm")
                        Switch(
                            checked = form.alarm,
                            onCheckedChange = { form = form.copy(alarm = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                uncheckedTrackColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }

            DatePickerFieldToModal(
                label = "Task date*",
                selectedDate = form.date,
                onDateSelected = { form = form.copy(date = it) })
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onCancel = {
                navController.navigate(MainCosplayScreen(uid = cosplayId, initialTab = 1)) {
                    popUpTo(MainCosplayScreen(uid = cosplayId)) { inclusive = true }
                }
            },
            onCommit = {
                cosplayViewModel.insertTask(form.toEntity(cosplayId = cosplayId))
            },
            postCommit = {
                navController.navigate(MainCosplayScreen(uid = cosplayId, initialTab = 1)) {
                    popUpTo(MainCosplayScreen(uid = cosplayId)) { inclusive = true }
                }
            },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}