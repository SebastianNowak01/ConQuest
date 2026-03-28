package com.example.conquest.screens.cosplay

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyAddFab
import com.example.conquest.components.MyDeleteFab
import com.example.conquest.components.MyImageBox
import com.example.conquest.components.MyOuterBox

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(elements) { element ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(32.dp)
                        )
                        .combinedClickable(onClick = {
                            if (selectionMode) {
                                val id = element.id
                                selectedIds =
                                    if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
                                if (selectedIds.isEmpty()) selectionMode = false
                            } else {
                                navController.navigate(EditElement(element.id))
                            }
                        }, onLongClick = {
                            selectionMode = true
                            selectedIds = selectedIds + element.id
                        }),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedIds.contains(element.id)) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.background
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
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
            }
        }
        MyAddFab(navController, route = NewElement(cosplayId))
    }
}