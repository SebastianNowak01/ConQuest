package com.maeldev.conquest.screens.cosplay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.toRoute
import com.maeldev.conquest.AppViewModelProvider
import com.maeldev.conquest.components.MyAddFab
import com.maeldev.conquest.components.MyEmptyState
import com.maeldev.conquest.components.MyLazyColumn
import com.maeldev.conquest.components.MyListItemActions
import com.maeldev.conquest.components.MyOuterBox
import com.maeldev.conquest.components.MySelectionCountLabel
import com.maeldev.conquest.components.MySelectionModeFabs
import com.maeldev.conquest.components.MyStatusChip
import com.maeldev.conquest.components.convertDateToString
import com.maeldev.conquest.components.rememberSelectionState
import com.maeldev.conquest.theme.UIConsts
import com.maeldev.conquest.viewmodel.TaskViewModel

@Composable
fun TasksTab(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
) {
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
            actions =
                MyListItemActions(
                    isSelected = { selection.isSelected(it.id) },
                    onClick = { task ->
                        if (!selection.isActive) {
                            navController.navigate(EditTask(task.id))
                            return@MyListItemActions
                        }
                        selection.toggle(task.id)
                    },
                    onLongClick = { task -> selection.select(task.id) },
                ),
        ) { task ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(UIConsts.spacingS),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.taskName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    task.date?.let { date ->
                        Text(
                            text = convertDateToString(date),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Bottom corner, icon only, exactly as an element states Ready and Bought: the
                // two tabs list the same kind of thing and should not each invent a layout.
                Row(
                    modifier = Modifier.align(Alignment.Bottom),
                    horizontalArrangement = Arrangement.spacedBy(UIConsts.paddingS),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MyStatusChip(
                        label = "Done",
                        icon = Icons.Default.CheckCircle,
                        active = task.done,
                        showLabel = false,
                    )
                    MyStatusChip(
                        label = "Reminder",
                        icon = Icons.Default.Notifications,
                        active = task.alarm,
                        showLabel = false,
                    )
                }
            }
        }

        if (tasks.isEmpty()) {
            MyEmptyState(
                icon = Icons.AutoMirrored.Filled.List,
                title = "No tasks yet",
                hint = "Add the steps this cosplay needs, and tick them off as you go.",
                modifier = Modifier.align(Alignment.Center),
            )
        }

        MySelectionCountLabel(selection = selection, itemLabelSingular = "task")

        MyAddFab(navController, route = NewTask(cosplayId))
    }
}
