package com.example.conquest.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cosplay_photos", foreignKeys = [ForeignKey(
        entity = Cosplay::class,
        parentColumns = ["uid"],
        childColumns = ["cosplay_id"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index(value = ["cosplay_id"])]
)
data class CosplayPhoto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "cosplay_id") val cosplayId: Int,
    @ColumnInfo(name = "path") val path: String
)