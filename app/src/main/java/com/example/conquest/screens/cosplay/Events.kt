package com.example.conquest.screens.cosplay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.EventListItem
import com.example.conquest.components.EventsFilters
import com.example.conquest.components.MyAddFab
import com.example.conquest.components.MyLazyColumn
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MySelectionModeFabs
import com.example.conquest.data.entity.EventType
import java.util.Calendar
import java.util.Date
import kotlinx.serialization.Serializable

@Serializable
object Events

@Composable
fun EventsScreen(
    navController: NavController,
    cosplayViewModel: CosplayViewModel = viewModel(),
) {
    val events by cosplayViewModel.events.collectAsState()
    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }
    var selectedType by remember { mutableStateOf<EventType?>(null) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }

    val filteredEvents = remember(events, selectedType, selectedDate) {
        val filterDate = selectedDate
        events.filter { event ->
            val matchesType = selectedType == null || event.eventType == selectedType
            val matchesDate = filterDate == null || isSameDay(event.eventDate, filterDate)
            matchesType && matchesDate
        }
    }

    LaunchedEffect(filteredEvents) {
        val visibleIds = filteredEvents.map { it.id }.toSet()
        selectedIds = selectedIds.intersect(visibleIds)
        if (selectedIds.isEmpty()) {
            selectionMode = false
        }
    }

    MyOuterBox {
        if (selectionMode) {
            MySelectionModeFabs(
                onExitSelection = {
                    selectionMode = false
                    selectedIds = emptySet()
                },
                onDeleteSelection = {
                    cosplayViewModel.deleteEventsByIds(selectedIds)
                    selectionMode = false
                    selectedIds = emptySet()
                },
                onSelectAll = {
                    selectedIds = filteredEvents.map { it.id }.toSet()
                    selectionMode = selectedIds.isNotEmpty()
                },
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            EventsFilters(
                selectedType = selectedType,
                selectedDate = selectedDate,
                onTypeSelected = { selectedType = it },
                onDateSelected = { selectedDate = it },
                onClearFilters = {
                    selectedType = null
                    selectedDate = null
                },
            )

            MyLazyColumn(
                items = filteredEvents,
                key = { it.id },
                isSelected = { selectedIds.contains(it.id) },
                onClick = { event ->
                    if (!selectionMode) {
                        navController.navigate(EditEvent(event.id))
                        return@MyLazyColumn
                    }
                    val id = event.id
                    selectedIds = if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
                    if (selectedIds.isEmpty()) {
                        selectionMode = false
                    }
                },
                onLongClick = { event ->
                    selectionMode = true
                    selectedIds = selectedIds + event.id
                },
            ) { event ->
                EventListItem(event = event)
            }
        }

        MyAddFab(navController = navController, route = NewEvent)
    }
}

private fun isSameDay(left: Date, right: Date): Boolean {
    val leftCalendar = Calendar.getInstance().apply { time = left }
    val rightCalendar = Calendar.getInstance().apply { time = right }
    return leftCalendar.get(Calendar.YEAR) == rightCalendar.get(Calendar.YEAR) &&
        leftCalendar.get(Calendar.DAY_OF_YEAR) == rightCalendar.get(Calendar.DAY_OF_YEAR)
}
