package com.maeldev.conquest.data

import androidx.room.TypeConverter
import com.maeldev.conquest.data.entity.EventType
import java.util.Calendar
import java.util.Date

/** Hour of the day, local time, at which task and event reminders fire. */
const val REMINDER_HOUR_OF_DAY = 9

/**
 * The instant this date's reminder should fire: [REMINDER_HOUR_OF_DAY]:00:00.000 local time.
 *
 * Reminders are stored as a plain date, so the time of day is applied at scheduling time. This
 * is the single definition of that rule — it used to be re-implemented at four call sites, where
 * changing the hour in only some of them would make a reminder fire at a different time after a
 * reboot than it did when the task was saved.
 */
fun Date.atReminderTime(): Long {
    return Calendar.getInstance().apply {
        time = this@atReminderTime
        set(Calendar.HOUR_OF_DAY, REMINDER_HOUR_OF_DAY)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

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
