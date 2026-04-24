package com.example.conquest.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.conquest.data.DateConverter
import com.example.conquest.data.dao.CosplayDao
import com.example.conquest.data.dao.CosplayElementDao
import com.example.conquest.data.dao.CosplayPhotoDao
import com.example.conquest.data.dao.CosplayTaskDao
import com.example.conquest.data.dao.EventDao
import com.example.conquest.data.dao.ProgressPhotoDao
import com.example.conquest.data.entity.Cosplay
import com.example.conquest.data.entity.CosplayElement
import com.example.conquest.data.entity.CosplayPhoto
import com.example.conquest.data.entity.CosplayTask
import com.example.conquest.data.entity.Event
import com.example.conquest.data.entity.EventCosplayCrossRef
import com.example.conquest.data.entity.ProgressPhoto

@Database(
    entities = [
        Cosplay::class,
        CosplayPhoto::class,
        CosplayElement::class,
        CosplayTask::class,
        Event::class,
        EventCosplayCrossRef::class,
        ProgressPhoto::class,
    ],
    version = 12,
)
@TypeConverters(DateConverter::class)
abstract class CosplayDatabase : RoomDatabase() {
    abstract fun cosplayDao(): CosplayDao
    abstract fun cosplayPhotoDao(): CosplayPhotoDao
    abstract fun cosplayElementDao(): CosplayElementDao
    abstract fun cosplayTaskDao(): CosplayTaskDao
    abstract fun eventDao(): EventDao
    abstract fun progressPhotoDao(): ProgressPhotoDao
}

