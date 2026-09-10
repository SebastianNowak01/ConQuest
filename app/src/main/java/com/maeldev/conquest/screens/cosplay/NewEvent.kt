package com.maeldev.conquest.screens.cosplay

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.components.EventFormContent
import com.maeldev.conquest.data.classes.EventFormState
import com.maeldev.conquest.data.classes.FormActions
import com.maeldev.conquest.viewmodel.EventViewModel
import kotlinx.serialization.Serializable

@Serializable
object NewEvent

@Composable
fun NewEvent(navController: NavController) {
    val eventViewModel: EventViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val snackbarHostState = remember { SnackbarHostState() }

    val baseline = remember { EventFormState() }
    var form by remember { mutableStateOf(baseline) }

    EventFormContent(
        title = "New Event",
        form = form,
        onFormChange = { form = it },
        actions =
            FormActions(
                snackbarHostState = snackbarHostState,
                isDirty = form != baseline,
                onCancel = { navController.popBackStack() },
                onCommit = {
                    eventViewModel.insertEvent(form.toEntity(id = 0))
                },
                postCommit = { navController.popBackStack() },
            ),
    )
}
