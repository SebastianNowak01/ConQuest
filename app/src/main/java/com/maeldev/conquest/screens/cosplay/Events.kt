package com.maeldev.conquest.screens.cosplay

import com.maeldev.conquest.AppViewModelProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.viewmodel.EventViewModel
import com.maeldev.conquest.components.EventListItem
import com.maeldev.conquest.components.MyAddFab
import com.maeldev.conquest.components.MyLazyColumn
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MySelectionModeFabs
import com.maeldev.conquest.components.rememberSelectionState
import com.maeldev.conquest.data.classes.CosplaySortOrder
import com.maeldev.conquest.data.classes.EventSortOption
import kotlinx.serialization.Serializable

@Serializable
object Events

@Composable
fun EventsScreen(
    navController: NavController,
    searchQuery: String,
    eventViewModel: EventViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val events by eventViewModel.events.collectAsState()
    val selectedType by eventViewModel.eventsFilterType.collectAsState()
    val selectedOrder by eventViewModel.eventsSortOrder.collectAsState()
    val selectedSortOption by eventViewModel.eventsSortOption.collectAsState()

    val filteredEvents = remember(events, selectedType, selectedOrder, selectedSortOption, searchQuery) {
        val normalizedSearchQuery = searchQuery.trim()
        val filtered = events.filter { event ->
            val matchesType = selectedType == null || event.eventType == selectedType
            val matchesSearch = normalizedSearchQuery.isBlank() ||
                event.eventName.contains(normalizedSearchQuery, ignoreCase = true) ||
                event.eventLocation.contains(normalizedSearchQuery, ignoreCase = true) ||
                event.description.orEmpty().contains(normalizedSearchQuery, ignoreCase = true)
            matchesType && matchesSearch
        }

        when (selectedSortOption) {
            EventSortOption.Alphabetical -> {
                if (selectedOrder == CosplaySortOrder.MostToLeast) {
                    filtered.sortedByDescending { it.eventName.lowercase() }
                } else {
                    filtered.sortedBy { it.eventName.lowercase() }
                }
            }
            EventSortOption.Date -> {
                if (selectedOrder == CosplaySortOrder.MostToLeast) {
                    filtered.sortedByDescending { it.eventDate }
                } else {
                    filtered.sortedBy { it.eventDate }
                }
            }
        }
    }

    val selection = rememberSelectionState(items = filteredEvents, id = { it.id })

    MyOuterBox {
        if (selection.isActive) {
            MySelectionModeFabs(
                selection = selection,
                itemLabelSingular = "event",
                itemLabelPlural = "events",
                onDeleteSelection = { ids -> eventViewModel.deleteEventsByIds(ids) },
            )
        }

        MyLazyColumn(
            items = filteredEvents,
            key = { it.id },
            isSelected = { selection.isSelected(it.id) },
            onClick = { event ->
                if (!selection.isActive) {
                    navController.navigate(EditEvent(event.id))
                    return@MyLazyColumn
                }
                selection.toggle(event.id)
            },
            onLongClick = { event -> selection.select(event.id) },
        ) { event ->
            EventListItem(event = event)
        }

        MyAddFab(navController = navController, route = NewEvent)
    }
}

