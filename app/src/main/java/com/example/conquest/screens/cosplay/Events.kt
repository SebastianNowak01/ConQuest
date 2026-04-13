package com.example.conquest.screens.cosplay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.DatePickerFieldToModal
import com.example.conquest.components.EventTypeDropdown
import com.example.conquest.components.MyAddFab
import com.example.conquest.components.MyDeleteFab
import com.example.conquest.components.MyLazyColumn
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.convertDateToString
import com.example.conquest.components.displayName
import com.example.conquest.data.entity.EventType
import com.example.conquest.ui.theme.UIConsts
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
            MyDeleteFab(
                onClick = {
                    cosplayViewModel.deleteEventsByIds(selectedIds)
                    selectionMode = false
                    selectedIds = emptySet()
                },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = UIConsts.paddingM),
        ) {
            EventTypeDropdown(
                selectedType = selectedType,
                onTypeSelected = { selectedType = it },
                label = "Filter by Type",
                allowAllOption = true,
            )

            DatePickerFieldToModal(
                label = "Filter by Date",
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
            )

            if (selectedType != null || selectedDate != null) {
                TextButton(onClick = {
                    selectedType = null
                    selectedDate = null
                }) {
                    Text(text = "Clear Filters")
                }
            }

            MyLazyColumn(
                modifier = Modifier.weight(1f),
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
                Text(
                    text = event.eventName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = event.eventLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = "${event.eventType.displayName} - ${convertDateToString(event.eventDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                if (!event.description.isNullOrBlank()) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
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
