package com.example.conquest.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.conquest.CosplayViewModel
import kotlinx.serialization.Serializable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import com.example.conquest.components.MyFab

@Serializable
object MainScreen

@Composable
fun MainScreen(navController: NavController, searchQuery: String) {
    val cosplayViewModel: CosplayViewModel = viewModel()
    val cosplays by cosplayViewModel.allCosplays.collectAsState()

    val filteredCosplays = remember(cosplays, searchQuery) {
        if (searchQuery.isBlank()) cosplays
        else cosplays.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectionMode) {
            MyFab(
                onClick = {
                    filteredCosplays.filter { selectedIds.contains(it.uid) }.forEach {
                        cosplayViewModel.deleteCosplay(it)
                    }
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
            items(filteredCosplays) { cosplay ->
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
                                val id = cosplay.uid
                                selectedIds =
                                    if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
                                if (selectedIds.isEmpty()) selectionMode = false
                            } else {
                                navController.navigate(MainCosplayScreen(cosplay.uid))
                            }
                        }, onLongClick = {
                            selectionMode = true
                            selectedIds = selectedIds + cosplay.uid
                        }),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedIds.contains(cosplay.uid)) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.background
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = cosplay.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        MyFab(
            onClick = { navController.navigate(NewCosplayScreen) },
            modifier = Modifier.align(Alignment.BottomCenter),
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.Add,
            contentDescription = "Add"
        )
    }
}