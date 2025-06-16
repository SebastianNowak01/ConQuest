package com.example.conquest.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.conquest.data.dao.CosplayDao
import com.example.conquest.data.entity.Cosplay
import com.example.conquest.data.entity.DateConverter

@Database(entities = [Cosplay::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class CosplayDatabase: RoomDatabase() {
    abstract fun cosplayDao(): CosplayDao
}