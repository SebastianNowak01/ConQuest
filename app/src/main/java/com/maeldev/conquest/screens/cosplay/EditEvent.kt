package com.maeldev.conquest.screens.cosplay

import com.maeldev.conquest.AppViewModelProvider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.viewmodel.EventViewModel
import com.maeldev.conquest.components.EventFormContent
import com.maeldev.conquest.data.classes.EventFormState
import kotlinx.serialization.Serializable

@Serializable
data class EditEvent(val eventId: Int)

@Composable
fun EditEvent(
    eventId: Int,
    navController: NavController,
    eventViewModel: EventViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val event by eventViewModel.getEventById(eventId).collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    
    var form by remember { mutableStateOf(EventFormState()) }

    LaunchedEffect(event?.id) {
        event?.let { loaded ->
            form = EventFormState.fromEntity(loaded)
        }
    }

    EventFormContent(
        title = "Edit Event",
        form = form,
        onFormChange = { form = it },
        snackbarHostState = snackbarHostState,
        onCancel = { navController.popBackStack() },
        onCommit = {
            val current = event ?: return@EventFormContent
            eventViewModel.updateEvent(form.toUpdatedEntity(current))
        },
        postCommit = { navController.popBackStack() }
    )
}
