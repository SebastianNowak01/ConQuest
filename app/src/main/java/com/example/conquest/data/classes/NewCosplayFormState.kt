package com.example.conquest.data.classes

import com.example.conquest.components.getCurrentDate
import com.example.conquest.data.entity.Cosplay
import java.util.Date

data class NewCosplayFormState(
    val characterName: String = "",
    val series: String = "",
    val initialDate: Date? = getCurrentDate(),
    val dueDate: Date? = getCurrentDate(),
    val budget: String = "",
    val inProgress: Boolean = true,
) {
    val budgetAmount: Double?
        get() = budget.takeIf { it.isNotBlank() }?.toDoubleOrNull()

    val isValid: Boolean
        get() = characterName.isNotBlank() && series.isNotBlank() && initialDate != null

    fun toEntity(
        uid: Int = 0, finished: Boolean = !inProgress
    ): Cosplay {
        return Cosplay(
            uid = uid,
            inProgress = inProgress,
            finished = finished,
            name = characterName,
            series = series,
            initialDate = requireNotNull(initialDate) { "Initial date required" },
            dueDate = dueDate,
            budget = budget.toDoubleOrNull()
        )
    }
}