package com.maeldev.conquest.screens.cosplay

import com.maeldev.conquest.AppViewModelProvider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.maeldev.conquest.viewmodel.TaskViewModel
import com.maeldev.conquest.components.MyAddFab
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MyLazyColumn
import com.maeldev.conquest.components.MySelectionModeFabs
import com.maeldev.conquest.components.rememberSelectionState
import com.maeldev.conquest.components.MySwitchCard
import com.maeldev.conquest.theme.UIConsts

@Composable
fun TasksTab(navController: NavController, navBackStackEntry: NavBackStackEntry) {
    val mainArgs = navBackStackEntry.toRoute<MainCosplayScreen>()
    val cosplayId = mainArgs.uid

    val taskViewModel: TaskViewModel = viewModel(factory = AppViewModelProvider.Factory)

    LaunchedEffect(cosplayId) {
        taskViewModel.setTaskCosplayId(cosplayId)
    }

    val tasks by taskViewModel.tasks.collectAsState()

    val selection = rememberSelectionState(items = tasks, id = { it.id })

    MyOuterBox {
        if (selection.isActive) {
            MySelectionModeFabs(
                selection = selection,
                itemLabelSingular = "task",
                itemLabelPlural = "tasks",
                onDeleteSelection = { ids -> taskViewModel.deleteTasksByIds(ids) },
            )
        }

        MyLazyColumn(
            items = tasks,
            key = { it.id },
            isSelected = { selection.isSelected(it.id) },
            onClick = { task ->
                if (!selection.isActive) {
                    navController.navigate(EditTask(task.id))
                    return@MyLazyColumn
                }
                selection.toggle(task.id)
            },
            onLongClick = { task -> selection.select(task.id) },
        ) { task ->
            Text(
                text = task.taskName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingS),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MySwitchCard(
                    label = "Done",
                    checked = task.done,
                    onCheckedChange = null,
                    modifier = Modifier.weight(1f),
                )
                MySwitchCard(
                    label = "Alarm",
                    checked = task.alarm,
                    onCheckedChange = null,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        MyAddFab(navController, route = NewTask(cosplayId))
    }
}
