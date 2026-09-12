package com.maeldev.conquest.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.maeldev.conquest.data.DateConverter
import java.util.Date

@Entity(tableName = "events")
@TypeConverters(DateConverter::class)
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "event_name") val eventName: String,
    @ColumnInfo(name = "event_location") val eventLocation: String,
    @ColumnInfo(name = "event_type") val eventType: EventType,
    @ColumnInfo(name = "event_date") val eventDate: Date,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "alarm") val alarm: Boolean = false,
)

/**
 * Kind of event, stored by name in `events.event_type`.
 *
 * [OTHER] is also the landing place for a stored value this build cannot read — see
 * [com.maeldev.conquest.data.DateConverter.stringToEventType]. Keep it last so the picker offers
 * the real kinds first.
 */
enum class EventType {
    EXPO,
    CONVENTION,
    CONTEST,
    MEETING,
    PARTY,
    OTHER,
}
