package com.maeldev.conquest.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maeldev.conquest.data.DateConverter
import com.maeldev.conquest.data.dao.CosplayDao
import com.maeldev.conquest.data.dao.CosplayElementDao
import com.maeldev.conquest.data.dao.CosplayPhotoDao
import com.maeldev.conquest.data.dao.CosplayTaskDao
import com.maeldev.conquest.data.dao.EventDao
import com.maeldev.conquest.data.dao.ProgressPhotoDao
import com.maeldev.conquest.data.entity.Cosplay
import com.maeldev.conquest.data.entity.CosplayElement
import com.maeldev.conquest.data.entity.CosplayPhoto
import com.maeldev.conquest.data.entity.CosplayTask
import com.maeldev.conquest.data.entity.Event
import com.maeldev.conquest.data.entity.EventCosplayCrossRef
import com.maeldev.conquest.data.entity.ProgressPhoto

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

