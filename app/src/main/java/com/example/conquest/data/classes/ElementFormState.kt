package com.example.conquest.data.classes

import com.example.conquest.data.entity.CosplayElement

data class ElementFormState(
    val name: String = "",
    val cost: String = "",
    val ready: Boolean = false,
    val highlight: Boolean = false,
    val bought: Boolean = false,
    val photoPath: String = "",
    val notes: String = "",
) {
    companion object {
        fun fromEntity(element: CosplayElement): ElementFormState {
            return ElementFormState(
                name = element.name,
                cost = element.cost?.toString() ?: "",
                ready = element.ready,
                highlight = element.highlight,
                bought = element.bought,
                photoPath = element.photoPath ?: "",
                notes = element.notes ?: "",
            )
        }
    }

    val costAmount: Double?
        get() = cost.takeIf { it.isNotBlank() }?.toDoubleOrNull()

    val isValid: Boolean
        get() = name.isNotBlank()

    fun toEntity(
        cosplayId: Int,
        id: Int = 0,
        notes: String? = this.notes.ifBlank { null },
    ): CosplayElement {
        return CosplayElement(
            id = id,
            cosplayId = cosplayId,
            name = name.trim(),
            cost = costAmount,
            ready = ready,
            photoPath = photoPath,
            highlight = highlight,
            bought = bought,
            notes = notes
        )
    }

    /**
     * For edit screens: apply form fields onto an existing entity without re-listing ids/foreign keys.
     */
    fun toUpdatedEntity(current: CosplayElement): CosplayElement {
        return current.copy(
            name = name.trim(),
            cost = costAmount,
            ready = ready,
            highlight = highlight,
            bought = bought,
            photoPath = photoPath,
            notes = this.notes.ifBlank { null },
        )
    }
}

