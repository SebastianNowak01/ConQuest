package com.example.conquest.data.classes

import com.example.conquest.data.entity.CosplayElement

data class NewElementFormState(
    val name: String = "",
    val cost: String = "",
    val ready: Boolean = false,
    val highlight: Boolean = false,
    val bought: Boolean = false,
    val photoPath: String = "",
) {
    val costAmount: Double?
        get() = cost.takeIf { it.isNotBlank() }?.toDoubleOrNull()

    val isValid: Boolean
        get() = name.isNotBlank()

    fun toEntity(
        cosplayId: Int,
        id: Int = 0,
        notes: String? = null,
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
}

