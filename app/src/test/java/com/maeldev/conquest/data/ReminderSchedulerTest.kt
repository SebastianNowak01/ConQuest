package com.maeldev.conquest.data

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Robolectric tests for [ReminderScheduler] schedule/cancel operations.
 * Uses Robolectric's shadow AlarmManager to verify alarms are set correctly.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReminderSchedulerTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun scheduleReminder_setsAlarmInFuture() {
        val futureTime = System.currentTimeMillis() + 60_000L // 1 minute from now

        ReminderScheduler.scheduleReminder(
            context = context,
            entityType = ReminderEntityType.TASK,
            entityId = 1,
            triggerAtMillis = futureTime,
            title = "Test Reminder",
            message = "Test Message",
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val shadowAlarmManager = Shadows.shadowOf(alarmManager)
        val scheduledAlarms = shadowAlarmManager.scheduledAlarms

        assertTrue("Expected at least one alarm to be scheduled", scheduledAlarms.isNotEmpty())

        val alarm = scheduledAlarms.last()
        assertEquals(AlarmManager.RTC_WAKEUP, alarm.type)
        assertEquals(futureTime, alarm.triggerAtTime)
    }

    @Test
    fun scheduleReminder_skipsAlarmInPast() {
        val pastTime = System.currentTimeMillis() - 60_000L // 1 minute ago

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val shadowAlarmManager = Shadows.shadowOf(alarmManager)
        val initialCount = shadowAlarmManager.scheduledAlarms.size

        ReminderScheduler.scheduleReminder(
            context = context,
            entityType = ReminderEntityType.TASK,
            entityId = 99,
            triggerAtMillis = pastTime,
            title = "Past Reminder",
            message = "Should be skipped",
        )

        assertEquals(
            "No new alarm should be scheduled for past time",
            initialCount,
            shadowAlarmManager.scheduledAlarms.size,
        )
    }

    @Test
    fun cancelReminder_doesNotCrashWhenNoPriorAlarm() {
        // Should not throw even if no alarm was previously set
        ReminderScheduler.cancelReminder(
            context = context,
            entityType = ReminderEntityType.EVENT,
            entityId = 999,
        )
    }

    @Test
    fun scheduleReminder_replacesExistingAlarmForSameEntity() {
        val time1 = System.currentTimeMillis() + 60_000L
        val time2 = System.currentTimeMillis() + 120_000L

        ReminderScheduler.scheduleReminder(
            context = context,
            entityType = ReminderEntityType.TASK,
            entityId = 5,
            triggerAtMillis = time1,
            title = "First",
            message = "First message",
        )

        ReminderScheduler.scheduleReminder(
            context = context,
            entityType = ReminderEntityType.TASK,
            entityId = 5,
            triggerAtMillis = time2,
            title = "Second",
            message = "Second message",
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val shadowAlarmManager = Shadows.shadowOf(alarmManager)

        // The PendingIntent uses FLAG_UPDATE_CURRENT, so the last schedule should win
        val lastAlarm = shadowAlarmManager.scheduledAlarms.last()
        assertEquals(time2, lastAlarm.triggerAtTime)
    }
}
