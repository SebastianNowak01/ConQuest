package com.maeldev.conquest

import android.app.Application
import androidx.room.Room
import com.maeldev.conquest.data.database.CosplayDatabase

class ConQuestApplication : Application() {
    val database: CosplayDatabase by lazy {
        Room.databaseBuilder(
            applicationContext, CosplayDatabase::class.java, "cosplays_database"
        ).fallbackToDestructiveMigration(true).build()
    }
}