package com.maeldev.conquest.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Version 13 was never released: the bump that added [com.maeldev.conquest.data.entity.Event.alarm]
 * went straight from 12 to 14, so one migration covers the whole gap. Builds handed out before
 * August 2026 sit at 12, and without this they crash on first database access rather than upgrading.
 */
private const val SCHEMA_VERSION_12 = 12
private const val SCHEMA_VERSION_14 = 14

val MIGRATION_12_14 =
    object : Migration(SCHEMA_VERSION_12, SCHEMA_VERSION_14) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE events ADD COLUMN alarm INTEGER NOT NULL DEFAULT 0")
        }
    }

/** Every migration the database ships with. Add new ones here so they are registered on the builder. */
val ALL_MIGRATIONS = arrayOf(MIGRATION_12_14)
