---
name: room-schema-change
description: The full checklist for changing the ConQuest Room database — adding or altering an entity, a column, an index or a DAO query that needs one. Use whenever editing anything under data/entity/, data/dao/, CosplayDatabase.kt or Migrations.kt, or when asked to "add a field", "add a table", "bump the database version", or "write a migration". Skipping a step here ships a crash on upgrade.
---

# Changing the ConQuest Room schema

Package root: `app/src/main/java/com/maeldev/conquest/data/`.

## Why this checklist exists

Version 13 was bumped and never released; the jump landed users on 14 with no registered
migration. Every existing install crashed on first database access. `MIGRATION_12_14` had to be
reconstructed by hand from the entity history because schemas 12 and 13 had never been exported.
Each step below is one of the things that went wrong.

## Checklist

**1. Edit the entity and/or DAO.**
Entities live in `data/entity/`, one file per table. Follow the existing shape: `@Entity` with an
explicit `tableName`, `@ColumnInfo(name = "snake_case")` on every field, `@ForeignKey` with
`onDelete = ForeignKey.CASCADE` for child rows, and an `@Index` on each foreign key column.
`CosplayTasks.kt` is a complete example. `Date` fields need `@TypeConverters(DateConverter::class)`.

**2. Bump `version` in `data/database/CosplayDatabase.kt`.**
Add the entity to the `entities = [...]` array and its DAO accessor if the table is new.

**3. Write the migration in `data/database/Migrations.kt` AND register it.**

```kotlin
private const val SCHEMA_VERSION_14 = 14
private const val SCHEMA_VERSION_15 = 15

val MIGRATION_14_15 =
    object : Migration(SCHEMA_VERSION_14, SCHEMA_VERSION_15) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE cosplay_tasks ADD COLUMN priority INTEGER NOT NULL DEFAULT 0")
        }
    }

val ALL_MIGRATIONS = arrayOf(MIGRATION_12_14, MIGRATION_14_15)
```

Appending to `ALL_MIGRATIONS` is the step that actually registers it on the builder. A migration
that exists but is not in that array does nothing. Detekt's `MagicNumber` rule is why the versions
are named constants.

Added columns must be `NOT NULL DEFAULT <x>` or nullable — SQLite cannot add a NOT NULL column
without a default. For anything SQLite's `ALTER TABLE` cannot do (dropping a column, changing a
type, adding a foreign key), use the create-new-table / copy / drop / rename dance.

**4. Build, then commit the exported schema.**
`./gradlew :app:assembleDebug` makes KSP write
`app/schemas/com.maeldev.conquest.data.database.CosplayDatabase/<version>.json`
(`room.schemaLocation` is set in `app/build.gradle.kts`).

**Commit that JSON.** `MigrationTest.currentSchemaIsExported` fails without it, and without it no
future migration can be validated against this version.

**5. Run the migration test** — it needs an emulator:

```sh
./gradlew :app:connectedDebugAndroidTest --tests '*MigrationTest'
```

`MigrationTest.migratesFromEveryExportedSchema` walks every committed schema forward through
`ALL_MIGRATIONS` and validates the result. If no device is available, say the migration is
unverified — do not report it as done.

**6. Never add `fallbackToDestructiveMigration`.** It makes the crash go away by deleting the
user's data. The database is built in `ConQuestApplication`; the migration array is the only
correct fix.

## Then

Run the `verify-changes` gate. A schema change usually also touches a `*FormState`, a ViewModel
and a screen — see `conquest-viewmodel` and `compose-screen`.
