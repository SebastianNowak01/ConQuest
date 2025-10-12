package com.example.conquest.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cosplay_elements", foreignKeys = [ForeignKey(
        entity = Cosplay::class,
        parentColumns = ["uid"],
        childColumns = ["cosplay_id"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index(value = ["cosplay_id"])]
)
data class CosplayElement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "cosplay_id") val cosplayId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "cost") val cost: Double?,
    @ColumnInfo(name = "ready") val ready: Boolean,
    @ColumnInfo(name = "photo_path") val photoPath: String?,
    @ColumnInfo(name = "highlight") val highlight: Boolean = false,
    @ColumnInfo(name = "bought") val bought: Boolean,
    @ColumnInfo(name = "notes") val notes: String?
)