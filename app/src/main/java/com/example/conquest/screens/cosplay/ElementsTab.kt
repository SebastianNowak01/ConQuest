package com.example.conquest.screens.cosplay

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyAddFab
import com.example.conquest.components.MyDeleteFab
import com.example.conquest.components.MyImageBox
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyLazyColumn

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

    MyOuterBox {
        if (selectionMode) {
            MyDeleteFab(
                onClick = {
                    cosplayViewModel.deleteElementsByIds(selectedIds)
                    selectionMode = false
                    selectedIds = emptySet()
                })
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
            // Elements used 12.dp padding inside the card; keep that.
            cardContentPadding = 12.dp,
        ) { element ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MyImageBox(
                    photoPath = element.photoPath.orEmpty(),
                    contentDescription = "Element image",
                    size = 48.dp,
                    clickable = false,
                    onClick = {},
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
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
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
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