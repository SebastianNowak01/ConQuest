---
name: create-migration
description: Guide for writing, registering, and testing Room database migrations in ConQuest.
---

# Room Database Migrations

Guide to writing and applying Room database migrations in ConQuest (`com.maeldev.conquest`).

---

## 1. Important Note on Destructive Migrations

In `app/src/main/java/com/maeldev/conquest/Application.kt`, Room is currently configured with:
```kotlin
Room.databaseBuilder(applicationContext, CosplayDatabase::class.java, "cosplays_database")
    .fallbackToDestructiveMigration(true)
    .build()
```
> [!WARNING]
> While `fallbackToDestructiveMigration(true)` prevents crashes during local development by clearing and recreating the database on schema mismatch, **it wipes all user data**. For production updates, write explicit `Migration` objects and ensure destructive fallback is removed or guarded.

---

## 2. Writing a Migration

Create migration objects in `app/src/main/java/com/maeldev/conquest/data/database/CosplayDatabase.kt` (or a dedicated `data/database/Migrations.kt` file):

```kotlin
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Migration SQL statements
    }
}
```

### Common SQL Operations

#### Adding a Column to an Existing Table
```kotlin
db.execSQL("ALTER TABLE cosplays ADD COLUMN notes TEXT DEFAULT NULL")

// Room requires NOT NULL columns to supply a DEFAULT value:
db.execSQL("ALTER TABLE cosplay_elements ADD COLUMN highlight INTEGER NOT NULL DEFAULT 0")
```

#### Creating a New Table
```kotlin
db.execSQL("""
    CREATE TABLE IF NOT EXISTS cosplay_items (
        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        cosplay_id INTEGER NOT NULL,
        name TEXT NOT NULL,
        notes TEXT,
        created_at INTEGER,
        FOREIGN KEY(cosplay_id) REFERENCES cosplays(uid) ON DELETE CASCADE
    )
""")

// Create indices matching the @Entity indices
db.execSQL("CREATE INDEX IF NOT EXISTS index_cosplay_items_cosplay_id ON cosplay_items(cosplay_id)")
```

### Room SQLite Type Mapping
- `Int`, `Long`, `Boolean`, `Date` -> `INTEGER` (Booleans are `0` or `1`, Dates are epoch timestamps)
- `Float`, `Double` -> `REAL`
- `String` -> `TEXT`
- `ByteArray` -> `BLOB`

---

## 3. Registering the Migration

### Step A: Update `Application.kt`
Add `.addMigrations(...)` to the `Room.databaseBuilder` chain in `app/src/main/java/com/maeldev/conquest/Application.kt` **before** any fallback methods:

```kotlin
val database: CosplayDatabase by lazy {
    Room.databaseBuilder(
        applicationContext,
        CosplayDatabase::class.java,
        "cosplays_database"
    )
    .addMigrations(MIGRATION_14_15)
    .fallbackToDestructiveMigration(true) // Note: Remove for production releases
    .build()
}
```

### Step B: Bump Database Version
In `app/src/main/java/com/maeldev/conquest/data/database/CosplayDatabase.kt`, increment the version matching the migration target version `Y`:

```kotlin
@Database(
    entities = [...],
    version = 15,
    exportSchema = false
)
```

---

## 4. Verification & Testing

1. **Verify Exact Schema Match**: SQLite columns, types, nullability, defaults, primary keys, and foreign keys must match Room's generated schema exactly, or Room will throw an `IllegalStateException` on startup.
2. **Test Upgrade**: Install an older version containing test data, run the app with the new migration applied, and verify that data is preserved and new columns/tables exist.
