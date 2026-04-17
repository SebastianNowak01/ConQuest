package com.example.conquest.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.example.conquest.CosplayViewModel
import com.example.conquest.data.classes.CosplaySortOption
import com.example.conquest.data.classes.CosplayStatusFilter

@Composable
private fun FilterButton(
    selectedFilter: CosplayStatusFilter,
    onFilterChange: (CosplayStatusFilter) -> Unit,
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
        CosplayStatusFilter.entries.forEach { filter ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = filter.label,
                        fontWeight = if (filter == selectedFilter) {
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
                    setExpanded(false)
                },
            )
        }
    }
}

@Composable
private fun SortButton(
    selectedSort: CosplaySortOption,
    onSortChange: (CosplaySortOption) -> Unit,
) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    MyIcon(
        onClick = { setExpanded(true) },
        imageVector = Icons.AutoMirrored.Filled.Sort,
        contentDescription = "Sort",
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { setExpanded(false) },
    ) {
        CosplaySortOption.entries.forEach { sort ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = sort.label,
                        fontWeight = if (sort == selectedSort) {
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
                    setExpanded(false)
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
    val owner = navBackStackEntry ?: return
    val cosplayViewModel: CosplayViewModel = viewModel(viewModelStoreOwner = owner)
    val selectedFilter by cosplayViewModel.mainScreenFilter.collectAsState()
    val selectedSort by cosplayViewModel.mainScreenSort.collectAsState()

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
        },
    )
}
