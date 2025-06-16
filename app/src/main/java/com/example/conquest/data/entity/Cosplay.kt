package com.example.conquest.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(tableName = "cosplays")
data class Cosplay(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "in_progress") val inProgress: Boolean,
    @ColumnInfo(name = "planned") val planned: Boolean,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "series") val series: String,
    @ColumnInfo(name = "initial_date") val initialDate: Date,
    @ColumnInfo(name = "due_date") val dueDate: Date?,
    @ColumnInfo(name = "budget") val budget: Long?
    )

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}