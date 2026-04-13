package com.example.conquest.data.classes

import com.example.conquest.components.getCurrentDate
import com.example.conquest.data.entity.Event
import com.example.conquest.data.entity.EventType
import java.util.Date

data class EventFormState(
    val eventName: String = "",
    val eventLocation: String = "",
    val eventType: EventType = EventType.EXPO,
    val eventDate: Date? = getCurrentDate(),
    val description: String = "",
) {
    companion object {
        fun fromEntity(event: Event): EventFormState {
            return EventFormState(
                eventName = event.eventName,
                eventLocation = event.eventLocation,
                eventType = event.eventType,
                eventDate = event.eventDate,
                description = event.description.orEmpty(),
            )
        }
    }

    val isValid: Boolean
        get() = eventName.isNotBlank() && eventLocation.isNotBlank() && eventDate != null

    fun toEntity(id: Int = 0): Event {
        return Event(
            id = id,
            eventName = eventName.trim(),
            eventLocation = eventLocation.trim(),
            eventType = eventType,
            eventDate = requireNotNull(eventDate) { "Event date required" },
            description = description.trim().ifBlank { null },
        )
    }

    fun toUpdatedEntity(current: Event): Event {
        return current.copy(
            eventName = eventName.trim(),
            eventLocation = eventLocation.trim(),
            eventType = eventType,
            eventDate = requireNotNull(eventDate) { "Event date required" },
            description = description.trim().ifBlank { null },
        )
    }
}

