package com.maeldev.conquest.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.maeldev.conquest.data.database.CosplayDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

enum class ReminderEntityType(val notificationTitle: String) {
    TASK("Task Reminder"),
    EVENT("Event Reminder"),
}

/**
 * Identifies the one reminder belonging to a given task or event.
 *
 * The type and the id are never useful apart — they are always paired to derive the alarm's
 * PendingIntent request code — so they travel as one value.
 */
data class ReminderTarget(val type: ReminderEntityType, val id: Int)

object ReminderScheduler {
    private const val TAG = "ReminderScheduler"

    fun scheduleReminder(
        context: Context,
        target: ReminderTarget,
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
            putExtra("notification_id", getRequestCode(target))
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(target),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        Log.d(TAG, "Scheduled reminder: $title at $triggerAtMillis")
    }

    /**
     * Schedules or cancels the reminder for an entity based on its current alarm flag and date.
     *
     * This is the whole "does this entity have a reminder" decision in one place; both the task
     * and event view models called an identical private copy of it.
     */
    fun syncReminder(
        context: Context,
        target: ReminderTarget,
        alarm: Boolean,
        date: Date?,
        message: String,
    ) {
        if (alarm && date != null) {
            scheduleReminder(
                context = context,
                target = target,
                triggerAtMillis = date.atReminderTime(),
                title = target.type.notificationTitle,
                message = message,
            )
        } else {
            cancelReminder(context, target)
        }
    }

    fun cancelReminder(
        context: Context,
        target: ReminderTarget,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(target),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Cancelled reminder for ${target.type.name} #${target.id}")
        }
    }

    suspend fun rescheduleAllReminders(
        context: Context,
        database: CosplayDatabase,
    ) {
        withContext(Dispatchers.IO) {
            database.cosplayTaskDao().getTasksWithActiveAlarms().forEach { task ->
                syncReminder(
                    context = context,
                    target = ReminderTarget(ReminderEntityType.TASK, task.id),
                    alarm = task.alarm,
                    date = task.date,
                    message = task.taskName,
                )
            }

            database.eventDao().getEventsWithActiveAlarms().forEach { event ->
                syncReminder(
                    context = context,
                    target = ReminderTarget(ReminderEntityType.EVENT, event.id),
                    alarm = event.alarm,
                    date = event.eventDate,
                    message = event.eventName,
                )
            }
        }
    }

    private fun getRequestCode(target: ReminderTarget): Int {
        return target.type.ordinal * 100_000 + target.id
    }
}
