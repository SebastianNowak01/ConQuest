package com.example.conquest.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.conquest.data.DateConverter
import java.util.Date

@Entity(tableName = "progress_photos")
@TypeConverters(DateConverter::class)
data class ProgressPhoto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Date = Date(),
)

