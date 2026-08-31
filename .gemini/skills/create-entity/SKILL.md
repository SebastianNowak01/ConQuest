---
name: create-entity
description: Creates a new Room entity data class, DAO interface, and registers them in CosplayDatabase following ConQuest architectural patterns.
---

# Create Room Entity & DAO

Guide to creating a Room entity and DAO in ConQuest (`com.maeldev.conquest`).

---

## 1. Create the Entity

File path: `app/src/main/java/com/maeldev/conquest/data/entity/<EntityNamePlural>.kt` (e.g., `CosplayNotes.kt`)

### Rules
- Annotate with `@Entity(tableName = "snake_case_table_name")`.
- Primary key: `@PrimaryKey(autoGenerate = true) val id: Int = 0`.
- All fields must have explicit `@ColumnInfo(name = "snake_case_name")`.
- If the entity links to a Cosplay, add the foreign key and index to `@Entity`:
  ```kotlin
  @Entity(
      tableName = "cosplay_items",
      foreignKeys = [
          ForeignKey(
              entity = Cosplay::class,
              parentColumns = ["uid"], // Note: Cosplay primary key is 'uid'
              childColumns = ["cosplay_id"],
              onDelete = ForeignKey.CASCADE
          )
      ],
      indices = [Index(value = ["cosplay_id"])]
  )
  ```
- If the entity includes `Date` fields, ensure `@TypeConverters(DateConverter::class)` is accessible (imported from `com.maeldev.conquest.data.DateConverter`).

### Template
```kotlin
package com.maeldev.conquest.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "cosplay_items",
    foreignKeys = [
        ForeignKey(
            entity = Cosplay::class,
            parentColumns = ["uid"],
            childColumns = ["cosplay_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cosplay_id"])]
)
data class CosplayItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "cosplay_id") val cosplayId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Date? = null
)
```

---

## 2. Create the DAO Interface

File path: `app/src/main/java/com/maeldev/conquest/data/dao/<EntityName>Dao.kt` (e.g., `CosplayItemDao.kt`)

### Rules
- Annotate interface with `@Dao`.
- Use `Flow<T>` for observable queries and `suspend` for one-shot insert/update/delete/fetch queries.
- Delete operations take `ids: Set<Int>` using SQL `IN (:ids)`.
- Cosplay-associated entities should include:
  - `fun getForCosplay(cosplayId: Int): Flow<List<Entity>>`
  - `suspend fun getForCosplaysOnce(cosplayIds: Set<Int>): List<Entity>`

### Template
```kotlin
package com.maeldev.conquest.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.maeldev.conquest.data.entity.CosplayItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CosplayItemDao {
    @Insert
    suspend fun insertItem(item: CosplayItem): Long

    @Update
    suspend fun updateItem(item: CosplayItem)

    @Query("DELETE FROM cosplay_items WHERE id IN (:ids)")
    suspend fun deleteItemsByIds(ids: Set<Int>)

    @Query("SELECT * FROM cosplay_items WHERE id = :id LIMIT 1")
    fun getItemById(id: Int): Flow<CosplayItem?>

    @Query("SELECT * FROM cosplay_items")
    fun getAllItems(): Flow<List<CosplayItem>>

    @Query("SELECT * FROM cosplay_items WHERE cosplay_id = :cosplayId")
    fun getItemsForCosplay(cosplayId: Int): Flow<List<CosplayItem>>

    @Query("SELECT * FROM cosplay_items WHERE cosplay_id IN (:cosplayIds)")
    suspend fun getItemsForCosplaysOnce(cosplayIds: Set<Int>): List<CosplayItem>
}
```

---

## 3. Register in `CosplayDatabase.kt`

File path: `app/src/main/java/com/maeldev/conquest/data/database/CosplayDatabase.kt`

1. Add the entity to the `entities` array in `@Database(...)`.
2. Add an abstract getter method for the DAO: `abstract fun cosplayItemDao(): CosplayItemDao`.
3. Increment the database `version` number.

```kotlin
@Database(
    entities = [
        Cosplay::class,
        CosplayPhoto::class,
        CosplayElement::class,
        CosplayTask::class,
        Event::class,
        EventCosplayCrossRef::class,
        ProgressPhoto::class,
        CosplayItem::class // Added entity
    ],
    version = 15, // Bumped version
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class CosplayDatabase : RoomDatabase() {
    // ... existing DAOs
    abstract fun cosplayItemDao(): CosplayItemDao
}
```

---

## Checklist
- [ ] `@Entity(tableName = "...")` and `@PrimaryKey(autoGenerate = true)` configured
- [ ] `@ColumnInfo(name = "...")` used on every field (snake_case)
- [ ] Foreign keys to `Cosplay` reference parent column `uid` and child column `cosplay_id` with `ForeignKey.CASCADE` and matching index
- [ ] DAO includes Flow queries for UI and suspend functions for writes / one-shot queries
- [ ] Registered in `CosplayDatabase.kt` and version bumped
