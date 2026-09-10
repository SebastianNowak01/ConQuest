package com.maeldev.conquest.components

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.data.classes.CosplaySortOption
import com.maeldev.conquest.data.classes.CosplaySortOrder
import com.maeldev.conquest.data.classes.CosplayStatusFilter
import com.maeldev.conquest.viewmodel.CosplayViewModel

@Composable
private fun FilterButton(
    selectedFilter: CosplayStatusFilter,
    onFilterChange: (CosplayStatusFilter) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    MyIcon(
        onClick = { expanded = true },
        imageVector = Icons.Default.FilterList,
        contentDescription = "Filter",
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        CosplayStatusFilter.entries.forEach { filter ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = filter.label,
                        fontWeight =
                            if (filter == selectedFilter) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                    )
                },
                trailingIcon = {
                    if (filter == selectedFilter) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                        )
                    }
                },
                onClick = {
                    onFilterChange(filter)
                    expanded = false
                },
            )
        }
    }
}

@Composable
private fun OrderButton(
    selectedOrder: CosplaySortOrder,
    onOrderChange: (CosplaySortOrder) -> Unit,
) {
    val currentOrderLabel = selectedOrder.label
    val nextOrder =
        if (selectedOrder == CosplaySortOrder.MostToLeast) {
            CosplaySortOrder.LeastToMost
        } else {
            CosplaySortOrder.MostToLeast
        }
    val nextOrderLabel = nextOrder.label

    val rotation =
        if (selectedOrder == CosplaySortOrder.MostToLeast) {
            180f
        } else {
            0f
        }

    IconButton(onClick = { onOrderChange(nextOrder) }) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Order: $currentOrderLabel. Tap to switch to $nextOrderLabel",
            modifier = Modifier.graphicsLayer(rotationZ = rotation),
        )
    }
}

@Composable
private fun SortButton(
    selectedSort: CosplaySortOption,
    onSortChange: (CosplaySortOption) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    MyIcon(
        onClick = { expanded = true },
        imageVector = Icons.AutoMirrored.Filled.Sort,
        contentDescription = "Sort",
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        CosplaySortOption.entries.forEach { sort ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = sort.label,
                        fontWeight =
                            if (sort == selectedSort) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                    )
                },
                trailingIcon = {
                    if (sort == selectedSort) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                        )
                    }
                },
                onClick = {
                    onSortChange(sort)
                    expanded = false
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenTopAppBar(
    searchQuery: String,
    navBackStackEntry: NavBackStackEntry?,
    onSearchQueryChange: (String) -> Unit,
    onMenuClick: () -> Unit,
) {
    // Scope the ViewModel to the destination the bar belongs to, not to whatever owner happens
    // to be in scope here. This bar is composed in the Scaffold's topBar slot, outside the
    // NavHost, so a bare viewModel() call resolves to the Activity's store while MainScreen's
    // resolves to its NavBackStackEntry — two CosplayViewModels, and every filter or sort set
    // here was written to the one the list never reads.
    val entry = navBackStackEntry ?: return
    val cosplayViewModel: CosplayViewModel =
        viewModel(viewModelStoreOwner = entry, factory = AppViewModelProvider.Factory)
    val selectedFilter by cosplayViewModel.mainScreenFilter.collectAsState()
    val selectedSort by cosplayViewModel.mainScreenSort.collectAsState()
    val selectedOrder by cosplayViewModel.mainScreenSortOrder.collectAsState()

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
            FilterButton(
                selectedFilter = selectedFilter,
                onFilterChange = cosplayViewModel::setMainScreenFilter,
            )
            SortButton(
                selectedSort = selectedSort,
                onSortChange = cosplayViewModel::setMainScreenSort,
            )
            OrderButton(
                selectedOrder = selectedOrder,
                onOrderChange = cosplayViewModel::setMainScreenSortOrder,
            )
        },
    )
}
