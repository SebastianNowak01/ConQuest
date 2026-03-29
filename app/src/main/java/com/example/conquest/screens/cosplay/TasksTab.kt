package com.example.conquest.screens.cosplay

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyAddFab
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyDeleteFab
import com.example.conquest.components.MySwitchCard

@Composable
fun TasksTab(navController: NavController, navBackStackEntry: NavBackStackEntry) {
    val mainArgs = navBackStackEntry.toRoute<MainCosplayScreen>()
    val cosplayId = mainArgs.uid

    val cosplayViewModel: CosplayViewModel = viewModel()

    LaunchedEffect(cosplayId) {
        cosplayViewModel.setTaskCosplayId(cosplayId)
    }

    val tasks by cosplayViewModel.tasks.collectAsState()

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

    MyOuterBox {
        if (selectionMode) {
            MyDeleteFab(onClick = {
                cosplayViewModel.deleteTasksByIds(selectedIds)
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
            items(tasks) { task ->
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
                                val id = task.id
                                selectedIds =
                                    if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
                                if (selectedIds.isEmpty()) selectionMode = false
                            } else {
                                navController.navigate(EditTask(task.id))
                            }
                        }, onLongClick = {
                            selectionMode = true
                            selectedIds = selectedIds + task.id
                        }),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedIds.contains(task.id)) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.background
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = task.taskName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MySwitchCard(
                                label = "Done",
                                checked = task.done,
                                onCheckedChange = null,
                                modifier = Modifier.weight(1f)
                            )
                            MySwitchCard(
                                label = "Alarm",
                                checked = task.alarm,
                                onCheckedChange = null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
        MyAddFab(navController, route = NewTask(cosplayId))
    }
}