package com.example.conquest.screens

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
import com.example.conquest.components.MyAddFab
import com.example.conquest.components.MyCosplayRow
import com.example.conquest.components.MyLazyColumn
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MySelectionModeFabs
import com.example.conquest.screens.cosplay.MainCosplayScreen
import com.example.conquest.screens.cosplay.NewCosplay
import kotlinx.serialization.Serializable

@Serializable
object MainScreen

@Composable
fun MainScreen(
    navController: NavController,
    searchQuery: String,
) {
    val cosplayViewModel: CosplayViewModel = viewModel()
    val cosplays by cosplayViewModel.allCosplays.collectAsState()
    val selectedFilter by cosplayViewModel.mainScreenFilter.collectAsState()
    val selectedSort by cosplayViewModel.mainScreenSort.collectAsState()
    val selectedOrder by cosplayViewModel.mainScreenSortOrder.collectAsState()
    val allTasks by cosplayViewModel.allTasks.collectAsState()
    val allElements by cosplayViewModel.allElements.collectAsState()
    val events by cosplayViewModel.events.collectAsState()

    val filteredCosplays = remember(cosplays, searchQuery, selectedFilter) {
        filterMainScreenCosplays(
            cosplays = cosplays,
            searchQuery = searchQuery,
            selectedFilter = selectedFilter,
        )
    }

    val taskCountByCosplay = remember(allTasks) {
        buildTaskCountByCosplay(allTasks)
    }

    val endDateByCosplay = remember(allTasks) {
        buildEndDateByCosplay(allTasks)
    }

    val totalSpendByCosplay = remember(allElements) {
        buildTotalSpendByCosplay(allElements)
    }

    val totalTimeDaysByCosplay = remember(cosplays, endDateByCosplay) {
        buildTotalTimeDaysByCosplay(
            cosplays = cosplays,
            endDateByCosplay = endDateByCosplay,
        )
    }

    val sortedCosplays = remember(
        filteredCosplays,
        selectedSort,
        selectedOrder,
        taskCountByCosplay,
        endDateByCosplay,
        totalSpendByCosplay,
        totalTimeDaysByCosplay,
        events,
    ) {
        sortMainScreenCosplays(
            cosplays = filteredCosplays,
            sort = selectedSort,
            order = selectedOrder,
            taskCountByCosplay = taskCountByCosplay,
            endDateByCosplay = endDateByCosplay,
            totalSpendByCosplay = totalSpendByCosplay,
            totalTimeDaysByCosplay = totalTimeDaysByCosplay,
            eventsCount = events.size,
        )
    }

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(sortedCosplays) {
        val visibleIds = sortedCosplays.map { it.uid }.toSet()
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
                    cosplayViewModel.deleteCosplaysByIds(selectedIds)
                    selectionMode = false
                    selectedIds = emptySet()
                },
                onSelectAll = {
                    selectedIds = sortedCosplays.map { it.uid }.toSet()
                    selectionMode = selectedIds.isNotEmpty()
                },
                deleteDialogTitle = "Delete selected ${if (selectedIds.size == 1) "cosplay" else "cosplays"}?",
                deleteDialogMessage = "This will permanently delete ${selectedIds.size} selected ${if (selectedIds.size == 1) "cosplay" else "cosplays"}.",
            )
        }

        MyLazyColumn(
            items = sortedCosplays,
            key = { it.uid },
            isSelected = { selectedIds.contains(it.uid) },
            onClick = { cosplay ->
                if (!selectionMode) {
                    navController.navigate(MainCosplayScreen(cosplay.uid))
                    return@MyLazyColumn
                }
                val id = cosplay.uid
                selectedIds = if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
                if (selectedIds.isEmpty()) selectionMode = false
            },
            onLongClick = { cosplay ->
                selectionMode = true
                selectedIds = selectedIds + cosplay.uid
            },
        ) { cosplay ->
            MyCosplayRow(
                name = cosplay.name,
                series = cosplay.series,
                photoPath = cosplay.cosplayPhotoPath ?: "",
            )
        }
        MyAddFab(navController, route = NewCosplay)
    }
}