package com.maeldev.conquest.screens.cosplay

import com.maeldev.conquest.AppViewModelProvider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.viewmodel.EventViewModel
import com.maeldev.conquest.components.DatePickerFieldToModal
import com.maeldev.conquest.components.EventTypeDropdown
import com.maeldev.conquest.components.MyColumn
import com.maeldev.conquest.components.MyHeaderText
import com.maeldev.conquest.components.MyInputField
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MySaveCancelRow
import com.maeldev.conquest.components.MySnackbarHost
import com.maeldev.conquest.components.MySwitchCard

import com.maeldev.conquest.data.classes.EventFormState
import kotlinx.serialization.Serializable


@Serializable
object NewEvent

@Composable
fun NewEvent(navController: NavController) {
    val eventViewModel: EventViewModel = viewModel(factory = AppViewModelProvider.Factory)
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

            MySwitchCard(
                label = "Reminder",
                checked = form.alarm,
                onCheckedChange = { form = form.copy(alarm = it) }
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
            onCommit = { eventViewModel.insertEvent(form.toEntity(), form.cosplayIds) },
            postCommit = { navController.popBackStack() },
        )

        MySnackbarHost(hostState = snackbarHostState)
    }
}
