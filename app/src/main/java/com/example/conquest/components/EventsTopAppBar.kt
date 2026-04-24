package com.example.conquest.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.example.conquest.CosplayViewModel
import com.example.conquest.data.classes.CosplaySortOrder
import com.example.conquest.data.classes.EventSortOption
import com.example.conquest.data.entity.EventType

@Composable
private fun EventsFilterButton(
    selectedType: EventType?,
    onTypeChange: (EventType?) -> Unit,
) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    MyIcon(
        onClick = { setExpanded(true) },
        imageVector = Icons.Default.FilterList,
        contentDescription = "Filter",
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { setExpanded(false) },
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = "All",
                    fontWeight = if (selectedType == null) FontWeight.Bold else FontWeight.Normal,
                )
            },
            trailingIcon = {
                if (selectedType == null) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                    )
                }
            },
            onClick = {
                onTypeChange(null)
                setExpanded(false)
            },
        )

        EventType.entries.forEach { eventType ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = eventType.displayName,
                        fontWeight = if (eventType == selectedType) FontWeight.Bold else FontWeight.Normal,
                    )
                },
                trailingIcon = {
                    if (eventType == selectedType) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                        )
                    }
                },
                onClick = {
                    onTypeChange(eventType)
                    setExpanded(false)
                },
            )
        }
    }
}

@Composable
private fun EventsSortByButton(
    selectedSortOption: EventSortOption,
    onSortOptionChange: (EventSortOption) -> Unit,
) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    MyIcon(
        onClick = { setExpanded(true) },
        imageVector = Icons.AutoMirrored.Filled.Sort,
        contentDescription = "Sort by",
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { setExpanded(false) },
    ) {
        EventSortOption.entries.forEach { sortOption ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = sortOption.label,
                        fontWeight = if (sortOption == selectedSortOption) FontWeight.Bold else FontWeight.Normal,
                    )
                },
                trailingIcon = {
                    if (sortOption == selectedSortOption) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                        )
                    }
                },
                onClick = {
                    onSortOptionChange(sortOption)
                    setExpanded(false)
                },
            )
        }
    }
}

@Composable
private fun EventsOrderButton(
    selectedOrder: CosplaySortOrder,
    onOrderChange: (CosplaySortOrder) -> Unit,
) {
    val currentOrderLabel = selectedOrder.label
    val nextOrder = if (selectedOrder == CosplaySortOrder.MostToLeast) {
        CosplaySortOrder.LeastToMost
    } else {
        CosplaySortOrder.MostToLeast
    }
    val nextOrderLabel = nextOrder.label

    val rotation = if (selectedOrder == CosplaySortOrder.MostToLeast) 180f else 0f

    IconButton(onClick = { onOrderChange(nextOrder) }) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Order: $currentOrderLabel. Tap to switch to $nextOrderLabel",
            modifier = Modifier.graphicsLayer(rotationZ = rotation),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsTopAppBar(
    searchQuery: String,
    navBackStackEntry: NavBackStackEntry?,
    onSearchQueryChange: (String) -> Unit,
    onMenuClick: () -> Unit,
) {
    val owner = navBackStackEntry ?: return
    val cosplayViewModel: CosplayViewModel = viewModel(viewModelStoreOwner = owner)
    val selectedType by cosplayViewModel.eventsFilterType.collectAsState()
    val selectedOrder by cosplayViewModel.eventsSortOrder.collectAsState()
    val selectedSortOption by cosplayViewModel.eventsSortOption.collectAsState()

    TopAppBar(
        colors = topAppBarColorsObject(),
        title = {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SearchBar(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                )
            }
        },
        actions = {
            EventsFilterButton(
                selectedType = selectedType,
                onTypeChange = cosplayViewModel::setEventsFilterType,
            )
            EventsSortByButton(
                selectedSortOption = selectedSortOption,
                onSortOptionChange = cosplayViewModel::setEventsSortOption,
            )
            EventsOrderButton(
                selectedOrder = selectedOrder,
                onOrderChange = cosplayViewModel::setEventsSortOrder,
            )
        },
    )
}

