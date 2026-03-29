package com.example.conquest.screens.cosplay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.example.conquest.CosplayViewModel
import com.example.conquest.components.MyAddFab
import com.example.conquest.components.MyDeleteFab
import com.example.conquest.components.MyOuterBox
import com.example.conquest.components.MyLazyColumn
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

        MyLazyColumn(
            items = tasks,
            key = { it.id },
            isSelected = { selectedIds.contains(it.id) },
            onClick = { task ->
                if (!selectionMode) {
                    navController.navigate(EditTask(task.id))
                    return@MyLazyColumn
                }
                val id = task.id
                selectedIds = if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
                if (selectedIds.isEmpty()) selectionMode = false

            },
            onLongClick = { task ->
                selectionMode = true
                selectedIds = selectedIds + task.id
            },
        ) { task ->
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
                    onCheckedChange = {},
                    modifier = Modifier.weight(1f),
                )
                MySwitchCard(
                    label = "Alarm",
                    checked = task.alarm,
                    onCheckedChange = {},
                    modifier = Modifier.weight(1f),
                )
            }
        }

        MyAddFab(navController, route = NewTask(cosplayId))
    }
}
