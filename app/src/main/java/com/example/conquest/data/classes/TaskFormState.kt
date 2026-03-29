package com.example.conquest.data.classes

import com.example.conquest.components.getCurrentDate
import com.example.conquest.data.entity.CosplayTask
import java.util.Date

data class TaskFormState(
    val taskName: String = "",
    val done: Boolean = false,
    val alarm: Boolean = false,
    val date: Date? = getCurrentDate(),
) {
    companion object {
        fun fromEntity(task: CosplayTask): TaskFormState {
            return TaskFormState(
                taskName = task.taskName,
                done = task.done,
                alarm = task.alarm,
                date = task.date ?: getCurrentDate(),
            )
        }
    }

    val isValid: Boolean
        get() = taskName.isNotBlank() && date != null

    fun toEntity(
        cosplayId: Int,
        id: Int = 0,
        notes: String? = null,
    ): CosplayTask {
        return CosplayTask(
            id = id,
            cosplayId = cosplayId,
            done = done,
            taskName = taskName.trim(),
            alarm = alarm,
            notes = notes,
            date = requireNotNull(date) { "Task date required" },
        )
    }

    /**
     * For edit screens: apply form fields onto an existing entity without re-listing ids/foreign keys.
     */
    fun toUpdatedEntity(current: CosplayTask, notes: String? = current.notes): CosplayTask {
        return current.copy(
            taskName = taskName.trim(),
            done = done,
            alarm = alarm,
            notes = notes,
            date = requireNotNull(date) { "Task date required" },
        )
    }
}
