package com.maeldev.conquest.data.classes

import com.maeldev.conquest.components.getCurrentDate
import com.maeldev.conquest.data.entity.Cosplay
import java.util.Date

data class CosplayFormState(
    val characterName: String = "",
    val series: String = "",
    val cosplayPhotoPath: String = "",
    val initialDate: Date? = getCurrentDate(),
    val dueDate: Date? = getCurrentDate(),
    val budget: String = "",
    val status: CosplayStatus = CosplayStatus.InProgress,
) {
    companion object {
        fun fromEntity(cosplay: Cosplay): CosplayFormState {
            return CosplayFormState(
                characterName = cosplay.name,
                series = cosplay.series,
                cosplayPhotoPath = cosplay.cosplayPhotoPath ?: "",
                initialDate = cosplay.initialDate,
                dueDate = cosplay.dueDate,
                budget = cosplay.budget?.toString() ?: "",
                status = CosplayStatus.fromEntity(cosplay),
            )
        }
    }

    val budgetAmount: Double?
        get() = budget.takeIf { it.isNotBlank() }?.toDoubleOrNull()

    val isValid: Boolean
        get() = characterName.isNotBlank() && series.isNotBlank() && initialDate != null

    fun toEntity(uid: Int = 0): Cosplay {
        return Cosplay(
            uid = uid,
            inProgress = status.inProgressFlag,
            finished = status.finishedFlag,
            name = characterName,
            series = series,
            initialDate = requireNotNull(initialDate) { "Initial date required" },
            dueDate = dueDate,
            budget = budgetAmount,
            finishedDate = finishedDateFor(current = null),
            cosplayPhotoPath = cosplayPhotoPath.ifBlank { null },
        )
    }

    /**
     * For edit screens: apply form fields onto an existing entity without re-listing ids.
     */
    fun toUpdatedEntity(current: Cosplay): Cosplay {
        return current.copy(
            inProgress = status.inProgressFlag,
            finished = status.finishedFlag,
            name = characterName,
            series = series,
            initialDate = requireNotNull(initialDate) { "Initial date required" },
            dueDate = dueDate,
            budget = budgetAmount,
            finishedDate = finishedDateFor(current),
            cosplayPhotoPath = cosplayPhotoPath.ifBlank { null },
        )
    }

    /**
     * The day this cosplay was finished, stamped the first time the form saves it as Done.
     *
     * An existing stamp is never re-dated: editing a finished cosplay's budget a year later must
     * not move the day it was completed. Leaving Done clears it, so a reopened cosplay starts
     * counting again instead of keeping a stale end date.
     */
    private fun finishedDateFor(current: Cosplay?): Date? =
        when {
            status != CosplayStatus.Done -> null
            else -> current?.finishedDate ?: getCurrentDate()
        }
}
