package com.maeldev.conquest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeldev.conquest.data.ReminderEntityType
import com.maeldev.conquest.data.ReminderScheduler
import com.maeldev.conquest.data.dao.CosplayDao
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

    private suspend fun refreshCosplayStats(cosplayId: Int) {
        cosplayDao.recomputeStatsForCosplay(cosplayId)
    }

    private suspend fun refreshCosplayStats(cosplayIds: Set<Int>) {
        if (cosplayIds.isNotEmpty()) {
            cosplayDao.recomputeStatsForCosplays(cosplayIds)
        }
    }

    fun insertTask(task: CosplayTask) {
        viewModelScope.launch {
            val taskId = taskDao.insertTask(task).toInt()
            handleReminderScheduling(ReminderEntityType.TASK, taskId, task.alarm, task.date, "Task Reminder", task.taskName)
            refreshCosplayStats(task.cosplayId)
        }
    }

    fun deleteTasksByIds(ids: Set<Int>) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            ids.forEach { id -> ReminderScheduler.cancelReminder(context, ReminderEntityType.TASK, id) }
            val cosplayIds = taskDao.getCosplayIdsForTaskIdsOnce(ids).toSet()
            taskDao.deleteTasksByIds(ids)
            refreshCosplayStats(cosplayIds)
        }
    }

    fun getTaskById(id: Int): Flow<CosplayTask?> {
        return taskDao.getTaskById(id)
    }

    fun updateTask(task: CosplayTask) {
        viewModelScope.launch {
            taskDao.updateTask(task)
            handleReminderScheduling(ReminderEntityType.TASK, task.id, task.alarm, task.date, "Task Reminder", task.taskName)
            refreshCosplayStats(task.cosplayId)
        }
    }

    private fun handleReminderScheduling(
        entityType: ReminderEntityType,
        entityId: Int,
        alarm: Boolean,
        date: java.util.Date?,
        title: String,
        message: String,
    ) {
        val context = getApplication<Application>()
        if (alarm && date != null) {
            val cal = java.util.Calendar.getInstance().apply {
                time = date
                set(java.util.Calendar.HOUR_OF_DAY, 9)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            ReminderScheduler.scheduleReminder(
                context = context,
                entityType = entityType,
                entityId = entityId,
                triggerAtMillis = cal.timeInMillis,
                title = title,
                message = message,
            )
        } else {
            ReminderScheduler.cancelReminder(context, entityType, entityId)
        }
    }
}
