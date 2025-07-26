package com.example.conquest.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.example.conquest.components.MyFab
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.conquest.CosplayViewModel

@Composable
fun CosplayElementsTab(navController: NavController, navBackStackEntry: NavBackStackEntry) {
    val mainArgs = navBackStackEntry.toRoute<MainCosplayScreen>()
    val cosplayId = mainArgs.uid

    val cosplayViewModel: CosplayViewModel = viewModel()

    LaunchedEffect(cosplayId) {
        cosplayViewModel.setElementCosplayId(cosplayId)
    }

    val elements by cosplayViewModel.elements.collectAsState()

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectionMode) {
            MyFab(
                onClick = {
                    cosplayViewModel.deleteElementsByIds(selectedIds)
                    selectionMode = false
                    selectedIds = emptySet()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(2f),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.Close,
                contentDescription = "Delete",
            )
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
//                                navController.navigate(MainCosplayScreen(cosplay.uid))
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
                    shape = RoundedCornerShape(32.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp)
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
                        Row {
                            if (element.ready) Text(
                                "Ready", color = MaterialTheme.colorScheme.secondary
                            )
                            if (element.bought) Text(
                                " • Bought", color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }

        MyFab(
            onClick = { navController.navigate(NewCosplayElementScreen(cosplayId)) },
            modifier = Modifier.align(Alignment.BottomCenter),
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.Add,
            contentDescription = "Add"
        )
    }
}