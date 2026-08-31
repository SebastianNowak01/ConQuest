---
name: wire-reminder
description: Guide for adding alarm and reminder scheduling support to new or existing entities in ConQuest.
---

# Wire Entity Reminders & Alarms

Guide to adding reminder/alarm functionality to Room entities in ConQuest (`com.maeldev.conquest`).

---

## 1. Entity Requirements

The entity must have an `alarm: Boolean` flag and a `Date?` / `Date` trigger field.

```kotlin
@Entity(tableName = "cosplay_tasks")
data class CosplayTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "cosplay_id") val cosplayId: Int,
    @ColumnInfo(name = "task_name") val taskName: String,
    @ColumnInfo(name = "alarm") val alarm: Boolean = false,
    @ColumnInfo(name = "date") val date: Date? = null
)
```

---

## 2. DAO Active Alarms Query

File path: `app/src/main/java/com/maeldev/conquest/data/dao/<EntityName>Dao.kt`

Add a suspend query to fetch all entities that have active alarms enabled (`alarm = 1`):

```kotlin
@Query("SELECT * FROM cosplay_tasks WHERE alarm = 1")
suspend fun getTasksWithActiveAlarms(): List<CosplayTask>
```

---

## 3. Update `ReminderScheduler.kt`

File path: `app/src/main/java/com/maeldev/conquest/data/ReminderScheduler.kt`

### Step A: Add Enum Value
Add the new entity type to `ReminderEntityType`:
```kotlin
enum class ReminderEntityType {
    TASK, EVENT, NEW_TYPE
}
```
> [!NOTE]
> Request codes are calculated as `entityType.ordinal * 100_000 + entityId`. Adding new enum values at the end ensures unique request codes without collisions.

### Step B: Reschedule on Device Boot
In `rescheduleAllReminders(context: Context, database: CosplayDatabase)`, query the DAO and schedule alarms (defaulting to 9:00 AM on the target date):

```kotlin
suspend fun rescheduleAllReminders(context: Context, database: CosplayDatabase) {
    withContext(Dispatchers.IO) {
        val taskDao = database.cosplayTaskDao()
        val eventDao = database.eventDao()
        val newDao = database.newDao()

        // Existing reschedules...

        newDao.getEntitiesWithActiveAlarms().forEach { entity ->
            entity.dateField?.let { date ->
                val cal = java.util.Calendar.getInstance().apply {
                    time = date
                    set(java.util.Calendar.HOUR_OF_DAY, 9)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                scheduleReminder(
                    context = context,
                    entityType = ReminderEntityType.NEW_TYPE,
                    entityId = entity.id,
                    triggerAtMillis = cal.timeInMillis,
                    title = "Entity Reminder",
                    message = entity.displayName,
                )
            }
        }
    }
}
```

---

## 4. ViewModel Integration

File path: `app/src/main/java/com/maeldev/conquest/viewmodel/<EntityName>ViewModel.kt`

In the ViewModel:
1. Implement a helper function `handleReminderScheduling(...)` that checks `alarm` and date validity.
2. Call `handleReminderScheduling` on insert and update.
3. Call `ReminderScheduler.cancelReminder(...)` on deletion.

### Template
```kotlin
class EntityViewModel(
    application: Application,
    private val entityDao: EntityDao
) : AndroidViewModel(application) {

    fun insertEntity(entity: Entity) {
        viewModelScope.launch {
            val entityId = entityDao.insertEntity(entity).toInt()
            handleReminderScheduling(
                entityType = ReminderEntityType.NEW_TYPE,
                entityId = entityId,
                alarm = entity.alarm,
                date = entity.dateField,
                title = "Entity Reminder",
                message = entity.name
            )
        }
    }

    fun updateEntity(entity: Entity) {
        viewModelScope.launch {
            entityDao.updateEntity(entity)
            handleReminderScheduling(
                entityType = ReminderEntityType.NEW_TYPE,
                entityId = entity.id,
                alarm = entity.alarm,
                date = entity.dateField,
                title = "Entity Reminder",
                message = entity.name
            )
        }
    }

    fun deleteEntitiesByIds(ids: Set<Int>) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            ids.forEach { id ->
                ReminderScheduler.cancelReminder(context, ReminderEntityType.NEW_TYPE, id)
            }
            entityDao.deleteEntitiesByIds(ids)
        }
    }

    private fun handleReminderScheduling(
        entityType: ReminderEntityType,
        entityId: Int,
        alarm: Boolean,
        date: java.util.Date?,
        title: String,
        message: String,
    ) {
        val context = getApplication<Application>()
        if (alarm && date != null) {
            val cal = java.util.Calendar.getInstance().apply {
                time = date
                set(java.util.Calendar.HOUR_OF_DAY, 9)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            ReminderScheduler.scheduleReminder(
                context = context,
                entityType = entityType,
                entityId = entityId,
                triggerAtMillis = cal.timeInMillis,
                title = title,
                message = message,
            )
        } else {
            ReminderScheduler.cancelReminder(context, entityType, entityId)
        }
    }
}
```

---

## 5. Boot Receiver

`BootReceiver` (`app/src/main/java/com/maeldev/conquest/data/BootReceiver.kt`) automatically receives `ACTION_BOOT_COMPLETED` and invokes `ReminderScheduler.rescheduleAllReminders(...)`. No modifications to `BootReceiver` are required as long as Step 3 is completed.

---

## Checklist
- [ ] Entity has `alarm: Boolean` and `Date` fields
- [ ] DAO has `@Query("SELECT * FROM table_name WHERE alarm = 1")`
- [ ] `ReminderEntityType` enum updated with new type
- [ ] `ReminderScheduler.rescheduleAllReminders()` queries DAO and schedules active alarms
- [ ] ViewModel handles scheduling on insert/update and cancellation on delete or when alarm is toggled off
