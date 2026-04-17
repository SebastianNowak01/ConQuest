package com.example.conquest.screens.cosplay

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyAddFab
import com.example.conquest.components.MyImageBox
import com.example.conquest.components.MyLazyColumn
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MySelectionModeFabs
import com.example.conquest.ui.theme.UIConsts

@Composable
fun ElementsTab(navController: NavController, navBackStackEntry: NavBackStackEntry) {
    val mainArgs = navBackStackEntry.toRoute<MainCosplayScreen>()
    val cosplayId = mainArgs.uid

    val cosplayViewModel: CosplayViewModel = viewModel()

    LaunchedEffect(cosplayId) {
        cosplayViewModel.setElementCosplayId(cosplayId)
    }

    val elements by cosplayViewModel.elements.collectAsState()

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(elements) {
        val visibleIds = elements.map { it.id }.toSet()
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
                    cosplayViewModel.deleteElementsByIds(selectedIds)
                    selectionMode = false
                    selectedIds = emptySet()
                },
                onSelectAll = {
                    selectedIds = elements.map { it.id }.toSet()
                    selectionMode = selectedIds.isNotEmpty()
                },
            )
        }

        MyLazyColumn(
            items = elements,
            key = { it.id },
            isSelected = { selectedIds.contains(it.id) },
            onClick = { element ->
                if (!selectionMode) {
                    navController.navigate(EditElement(element.id))
                    return@MyLazyColumn
                }
                val id = element.id
                selectedIds = if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
                if (selectedIds.isEmpty()) selectionMode = false
            },
            onLongClick = { element ->
                selectionMode = true
                selectedIds = selectedIds + element.id
            },
            cardContentPadding = UIConsts.spacingS,
        ) { element ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingS)
            ) {
                MyImageBox(
                    photoPath = element.photoPath.orEmpty(),
                    contentDescription = "Element image",
                    size = UIConsts.imageSizeS,
                    clickable = false,
                    onClick = {},
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(UIConsts.paddingXS)
                ) {
                    Text(
                        text = element.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (element.cost != null) {
                        Text(
                            text = "Cost: $${element.cost}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(UIConsts.paddingXS),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (element.ready) {
                        Text(
                            "Ready",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    if (element.bought) {
                        Text(
                            "Bought",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
        MyAddFab(navController, route = NewElement(cosplayId))
    }
}