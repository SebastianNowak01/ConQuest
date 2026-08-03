package com.maeldev.conquest.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.maeldev.conquest.data.database.CosplayDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class ReminderEntityType {
    TASK, EVENT
}

object ReminderScheduler {
    private const val TAG = "ReminderScheduler"

    fun scheduleReminder(
        context: Context,
        entityType: ReminderEntityType,
        entityId: Int,
        triggerAtMillis: Long,
        title: String,
        message: String,
    ) {
        if (triggerAtMillis <= System.currentTimeMillis()) {
            Log.d(TAG, "Skipping reminder in the past: $title")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("notification_id", getRequestCode(entityType, entityId))
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(entityType, entityId),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        Log.d(TAG, "Scheduled reminder: $title at $triggerAtMillis")
    }

    fun cancelReminder(
        context: Context,
        entityType: ReminderEntityType,
        entityId: Int,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(entityType, entityId),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Cancelled reminder for ${entityType.name} #$entityId")
        }
    }

    suspend fun rescheduleAllReminders(context: Context, database: CosplayDatabase) {
        withContext(Dispatchers.IO) {
            val taskDao = database.cosplayTaskDao()
            val eventDao = database.eventDao()

            taskDao.getTasksWithActiveAlarms().forEach { task ->
                task.date?.let { date ->
                    val cal = java.util.Calendar.getInstance().apply {
                        time = date
                        set(java.util.Calendar.HOUR_OF_DAY, 9)
                        set(java.util.Calendar.MINUTE, 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }
                    scheduleReminder(
                        context = context,
                        entityType = ReminderEntityType.TASK,
                        entityId = task.id,
                        triggerAtMillis = cal.timeInMillis,
                        title = "Task Reminder",
                        message = task.taskName,
                    )
                }
            }

            eventDao.getEventsWithActiveAlarms().forEach { event ->
                event.eventDate?.let { date ->
                    val cal = java.util.Calendar.getInstance().apply {
                        time = date
                        set(java.util.Calendar.HOUR_OF_DAY, 9)
                        set(java.util.Calendar.MINUTE, 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }
                    scheduleReminder(
                        context = context,
                        entityType = ReminderEntityType.EVENT,
                        entityId = event.id,
                        triggerAtMillis = cal.timeInMillis,
                        title = "Event Reminder",
                        message = event.eventName,
                    )
                }
            }
        }
    }

    private fun getRequestCode(entityType: ReminderEntityType, entityId: Int): Int {
        return entityType.ordinal * 100_000 + entityId
    }
}
