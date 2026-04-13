package com.example.conquest.data

import androidx.room.TypeConverter
import com.example.conquest.data.entity.EventType
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun eventTypeToString(eventType: EventType?): String? {
        return eventType?.name
    }

    @TypeConverter
    fun stringToEventType(value: String?): EventType? {
        return value?.let { EventType.valueOf(it) }
    }
}

