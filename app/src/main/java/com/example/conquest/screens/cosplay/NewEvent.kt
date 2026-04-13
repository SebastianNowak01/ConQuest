package com.example.conquest.screens.cosplay

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.DatePickerFieldToModal
import com.example.conquest.components.EventTypeDropdown
import com.example.conquest.components.MyColumn
import com.example.conquest.components.MyHeaderText
import com.example.conquest.components.MyInputField
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MySaveCancelRow
import com.example.conquest.components.MySnackbarHost
import com.example.conquest.data.classes.EventFormState
import kotlinx.serialization.Serializable

@Serializable
object NewEvent

@Composable
fun NewEvent(navController: NavController) {
    val cosplayViewModel: CosplayViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    var form by remember { mutableStateOf(EventFormState()) }

    MyOuterBox {
        MyColumn {
            MyHeaderText(text = "Add Event")

            MyInputField(
                value = form.eventName,
                onValueChange = { form = form.copy(eventName = it) },
                label = "Event Name*",
            )

            MyInputField(
                value = form.eventLocation,
                onValueChange = { form = form.copy(eventLocation = it) },
                label = "Event Location*",
            )

            EventTypeDropdown(
                selectedType = form.eventType,
                onTypeSelected = { type ->
                    type?.let { form = form.copy(eventType = it) }
                },
            )

            DatePickerFieldToModal(
                label = "Date*",
                selectedDate = form.eventDate,
                onDateSelected = { form = form.copy(eventDate = it) },
            )

            MyInputField(
                value = form.description,
                onValueChange = { form = form.copy(description = it) },
                label = "Description (Optional)",
                singleLine = false,
            )
        }

        MySaveCancelRow(
            snackbarHostState = snackbarHostState,
            isValid = form.isValid,
            onCancel = { navController.popBackStack() },
            onCommit = { cosplayViewModel.insertEvent(form.toEntity()) },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
