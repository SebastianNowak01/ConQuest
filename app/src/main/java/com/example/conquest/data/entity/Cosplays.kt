package com.example.conquest.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.conquest.data.DateConverter
import java.util.Date

@Entity(tableName = "cosplays")
@TypeConverters(DateConverter::class)
data class Cosplay(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "in_progress") val inProgress: Boolean,
    @ColumnInfo(name = "finished") val finished: Boolean,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "series") val series: String,
    @ColumnInfo(name = "initial_date") val initialDate: Date,
    @ColumnInfo(name = "due_date") val dueDate: Date?,
    @ColumnInfo(name = "budget") val budget: Double?
)
