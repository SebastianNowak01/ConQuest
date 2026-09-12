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
private const val SCHEMA_VERSION_15 = 15

val MIGRATION_12_14 =
    object : Migration(SCHEMA_VERSION_12, SCHEMA_VERSION_14) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE events ADD COLUMN alarm INTEGER NOT NULL DEFAULT 0")
        }
    }

/**
 * Records *when* a cosplay was finished. Until now nothing stored that, so the "days worked"
 * statistic could only measure the planned span between the initial date and the last task or due
 * date — a number that never moved while the cosplay was actually being worked on.
 *
 * Nullable rather than backfilled: rows already marked finished carry no evidence of the day it
 * happened, and inventing one would be indistinguishable from a real stamp. Those rows fall back
 * to the old planned-span estimate instead — see
 * [com.maeldev.conquest.data.classes.daysWorked].
 */
val MIGRATION_14_15 =
    object : Migration(SCHEMA_VERSION_14, SCHEMA_VERSION_15) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE cosplays ADD COLUMN finished_date INTEGER")
        }
    }

/** Every migration the database ships with. Add new ones here so they are registered on the builder. */
val ALL_MIGRATIONS = arrayOf(MIGRATION_12_14, MIGRATION_14_15)
