package com.maeldev.conquest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeldev.conquest.data.ReminderEntityType
import com.maeldev.conquest.data.ReminderScheduler
import com.maeldev.conquest.data.ReminderTarget
import com.maeldev.conquest.data.dao.CosplayDao
import com.maeldev.conquest.data.dao.refreshStatsFor
import com.maeldev.conquest.data.dao.CosplayTaskDao
import com.maeldev.conquest.data.entity.CosplayTask
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    application: Application,
    private val taskDao: CosplayTaskDao,
    private val cosplayDao: CosplayDao
) : AndroidViewModel(application) {

    val allTasks: StateFlow<List<CosplayTask>> =
        taskDao.getAllTasks().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _taskCosplayId = MutableStateFlow<Int?>(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<CosplayTask>> =
        _taskCosplayId.filterNotNull().flatMapLatest { id -> taskDao.getTasksForCosplay(id) }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setTaskCosplayId(id: Int) {
        _taskCosplayId.value = id
    }

    fun insertTask(task: CosplayTask) {
        viewModelScope.launch {
            val taskId = taskDao.insertTask(task).toInt()
            ReminderScheduler.syncReminder(
                context = getApplication(),
                target = ReminderTarget(ReminderEntityType.TASK, taskId),
                alarm = task.alarm,
                date = task.date,
                message = task.taskName,
            )
            cosplayDao.refreshStatsFor(task.cosplayId)
        }
    }

    fun deleteTasksByIds(ids: Set<Int>) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            ids.forEach { id -> ReminderScheduler.cancelReminder(context, ReminderTarget(ReminderEntityType.TASK, id)) }
            val cosplayIds = taskDao.getCosplayIdsForTaskIdsOnce(ids).toSet()
            taskDao.deleteTasksByIds(ids)
            cosplayDao.refreshStatsFor(cosplayIds)
        }
    }

    fun getTaskById(id: Int): Flow<CosplayTask?> {
        return taskDao.getTaskById(id)
    }

    fun updateTask(task: CosplayTask) {
        viewModelScope.launch {
            taskDao.updateTask(task)
            ReminderScheduler.syncReminder(
                context = getApplication(),
                target = ReminderTarget(ReminderEntityType.TASK, task.id),
                alarm = task.alarm,
                date = task.date,
                message = task.taskName,
            )
            cosplayDao.refreshStatsFor(task.cosplayId)
        }
    }

}
