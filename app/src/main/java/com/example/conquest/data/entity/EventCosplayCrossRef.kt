package com.example.conquest.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "event_cosplay_cross_ref",
    primaryKeys = ["event_id", "cosplay_id"],
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["event_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Cosplay::class,
            parentColumns = ["uid"],
            childColumns = ["cosplay_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["event_id"]), Index(value = ["cosplay_id"])],
)
data class EventCosplayCrossRef(
    @ColumnInfo(name = "event_id") val eventId: Int,
    @ColumnInfo(name = "cosplay_id") val cosplayId: Int,
)

