package com.maeldev.conquest.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.CosplayViewModel
import com.maeldev.conquest.components.MyAddFab
import com.maeldev.conquest.components.MyCosplayRow
import com.maeldev.conquest.components.MyLazyColumn
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MySelectionModeFabs
import com.maeldev.conquest.screens.cosplay.MainCosplayScreen
import com.maeldev.conquest.screens.cosplay.NewCosplay
import kotlinx.serialization.Serializable
import kotlin.collections.minus
import kotlin.collections.plus

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

    val filteredCosplays = remember(cosplays, searchQuery, selectedFilter) {
        filterMainScreenCosplays(
            cosplays = cosplays,
            searchQuery = searchQuery,
            selectedFilter = selectedFilter,
        )
    }

    val sortedCosplays = remember(
        filteredCosplays,
        selectedSort,
        selectedOrder,
    ) {
        sortMainScreenCosplays(
            cosplays = filteredCosplays,
            sort = selectedSort,
            order = selectedOrder,
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
                    navController.navigate(
                        MainCosplayScreen(
                            cosplay.uid
                        )
                    )
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
        MyAddFab(
            navController,
            route = NewCosplay
        )
    }
}