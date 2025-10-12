package com.example.conquest.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cosplay_tasks", foreignKeys = [ForeignKey(
        entity = Cosplay::class,
        parentColumns = ["uid"],
        childColumns = ["cosplay_id"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index(value = ["cosplay_id"])]
)
data class CosplayTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "cosplay_id") val cosplayId: Int,
    @ColumnInfo(name = "task_name") val taskName: String,
    @ColumnInfo(name = "done") val done: Boolean,
    @ColumnInfo(name = "alarm") val alarm: Boolean,
    @ColumnInfo(name = "notes") val notes: String?
)