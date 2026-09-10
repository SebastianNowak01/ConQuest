package com.maeldev.conquest.data

import androidx.room.TypeConverter
import com.maeldev.conquest.data.entity.EventType
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

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

/**
 * Material's date picker holds its selection as UTC midnight, while every date this app stores,
 * formats and compares is a [Date] read in the default zone. Converting on only one side of that
 * boundary shifts the day: east of UTC a date picked late in the evening opens on the day before,
 * west of it the day the user taps comes back as the day before.
 *
 * These two functions are that boundary, and are inverses of each other.
 */
fun Date.toPickerMillis(): Long {
    val local = Calendar.getInstance().apply { time = this@toPickerMillis }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

fun pickerMillisToDate(millis: Long): Date {
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
    return Calendar.getInstance().apply {
        clear()
        set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH))
    }.time
}

/**
 * True when this date falls on a day before today.
 *
 * Dates in this app are calendar days, not instants: they are stored at midnight. Comparing them
 * with Date.before(now) therefore calls anything happening *today* "past" from one minute after
 * midnight onwards, which is not what a user means by an event having happened. Both sides are
 * reduced to their calendar day before comparing.
 */
fun Date.isBeforeToday(): Boolean {
    return toPickerMillis() < Date().toPickerMillis()
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
