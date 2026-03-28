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
}
