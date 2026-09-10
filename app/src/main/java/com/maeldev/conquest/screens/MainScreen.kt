package com.maeldev.conquest.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.components.MyAddFab
import com.maeldev.conquest.components.MyCosplayRow
import com.maeldev.conquest.components.MyEmptyState
import com.maeldev.conquest.components.MyLazyColumn
import com.maeldev.conquest.components.MyListItemActions
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MySelectionCountLabel
import com.maeldev.conquest.components.MySelectionModeFabs
import com.maeldev.conquest.components.rememberSelectionState
import com.maeldev.conquest.screens.cosplay.MainCosplayScreen
import com.maeldev.conquest.screens.cosplay.NewCosplay
import com.maeldev.conquest.viewmodel.CosplayViewModel
import kotlinx.serialization.Serializable

@Serializable
object MainScreen

@Composable
fun MainScreen(
    navController: NavController,
    searchQuery: String,
) {
    val cosplayViewModel: CosplayViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val cosplays by cosplayViewModel.allCosplays.collectAsState()
    val selectedFilter by cosplayViewModel.mainScreenFilter.collectAsState()
    val selectedSort by cosplayViewModel.mainScreenSort.collectAsState()
    val selectedOrder by cosplayViewModel.mainScreenSortOrder.collectAsState()

    val filteredCosplays =
        remember(cosplays, searchQuery, selectedFilter) {
            filterMainScreenCosplays(
                cosplays = cosplays,
                searchQuery = searchQuery,
                selectedFilter = selectedFilter,
            )
        }

    val sortedCosplays =
        remember(
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

    val selection = rememberSelectionState(items = sortedCosplays, id = { it.uid })

    MyOuterBox {
        if (selection.isActive) {
            MySelectionModeFabs(
                selection = selection,
                itemLabelSingular = "cosplay",
                itemLabelPlural = "cosplays",
                onDeleteSelection = { ids -> cosplayViewModel.deleteCosplaysByIds(ids) },
            )
        }

        MyLazyColumn(
            items = sortedCosplays,
            key = { it.uid },
            actions =
                MyListItemActions(
                    isSelected = { selection.isSelected(it.uid) },
                    onClick = { cosplay ->
                        if (!selection.isActive) {
                            navController.navigate(MainCosplayScreen(cosplay.uid))
                            return@MyListItemActions
                        }
                        selection.toggle(cosplay.uid)
                    },
                    onLongClick = { cosplay -> selection.select(cosplay.uid) },
                ),
        ) { cosplay ->
            MyCosplayRow(cosplay = cosplay)
        }

        if (sortedCosplays.isEmpty()) {
            val nothingSaved = cosplays.isEmpty()
            val emptyHint =
                if (nothingSaved) {
                    "Start a project and track its elements, tasks and progress."
                } else {
                    "No cosplay matches the current search and filter."
                }

            MyEmptyState(
                icon = Icons.Default.TheaterComedy,
                title = if (nothingSaved) "No cosplays yet" else "Nothing matches",
                hint = emptyHint,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        MySelectionCountLabel(selection = selection, itemLabelSingular = "cosplay")

        MyAddFab(
            navController,
            route = NewCosplay,
        )
    }
}
