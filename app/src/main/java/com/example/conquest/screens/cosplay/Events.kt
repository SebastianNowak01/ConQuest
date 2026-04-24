package com.example.conquest.screens.cosplay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.EventListItem
import com.example.conquest.components.MyAddFab
import com.example.conquest.components.MyLazyColumn
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MySelectionModeFabs
import com.example.conquest.data.classes.CosplaySortOrder
import com.example.conquest.data.classes.EventSortOption
import kotlinx.serialization.Serializable

@Serializable
object Events

@Composable
fun EventsScreen(
    navController: NavController,
    searchQuery: String,
    cosplayViewModel: CosplayViewModel = viewModel(),
) {
    val events by cosplayViewModel.events.collectAsState()
    val selectedType by cosplayViewModel.eventsFilterType.collectAsState()
    val selectedOrder by cosplayViewModel.eventsSortOrder.collectAsState()
    val selectedSortOption by cosplayViewModel.eventsSortOption.collectAsState()
    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

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
                deleteDialogTitle = "Delete selected ${if (selectedIds.size == 1) "event" else "events"}?",
                deleteDialogMessage = "This will permanently delete ${selectedIds.size} selected ${if (selectedIds.size == 1) "event" else "events"}.",
            )
        }

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

        MyAddFab(navController = navController, route = NewEvent)
    }
}

