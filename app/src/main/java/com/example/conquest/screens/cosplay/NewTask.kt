package com.example.conquest.screens.cosplay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.DatePickerFieldToModal
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MySnackbarHost
import com.example.conquest.components.MySwitchCard
import com.example.conquest.data.classes.TaskFormState
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

    var form by remember { mutableStateOf(TaskFormState()) }

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "Add Task")

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
                MySwitchCard(
                    label = "Done",
                    checked = form.done,
                    onCheckedChange = { form = form.copy(done = it) },
                    modifier = Modifier.weight(1f),
                )
                MySwitchCard(
                    label = "Alarm",
                    checked = form.alarm,
                    onCheckedChange = { form = form.copy(alarm = it) },
                    modifier = Modifier.weight(1f),
                )
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