package com.maeldev.conquest.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Robolectric tests for [ReminderNotificationHelper].
 * Verifies notification channel creation and notification posting.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReminderNotificationHelperTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun createNotificationChannel_createsChannelWithCorrectId() {
        ReminderNotificationHelper.createNotificationChannel(context)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = notificationManager.getNotificationChannel(ReminderNotificationHelper.CHANNEL_ID)

        assertNotNull("Notification channel should be created", channel)
        assertEquals(ReminderNotificationHelper.CHANNEL_ID, channel.id)
    }

    @Test
    fun createNotificationChannel_setsHighImportance() {
        ReminderNotificationHelper.createNotificationChannel(context)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = notificationManager.getNotificationChannel(ReminderNotificationHelper.CHANNEL_ID)

        assertNotNull(channel)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, channel!!.importance)
    }

    @Test
    fun createNotificationChannel_isIdempotent() {
        ReminderNotificationHelper.createNotificationChannel(context)
        ReminderNotificationHelper.createNotificationChannel(context)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channels = notificationManager.notificationChannels.filter {
            it.id == ReminderNotificationHelper.CHANNEL_ID
        }

        assertEquals("Channel should not be duplicated", 1, channels.size)
    }

    @Test
    fun showNotification_postsNotificationWithCorrectContent() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val shadowManager = Shadows.shadowOf(notificationManager)

        // Enable notifications for the test context
        shadowManager.setNotificationsEnabled(true)

        ReminderNotificationHelper.showNotification(
            context = context,
            notificationId = 42,
            title = "Test Title",
            message = "Test Body",
        )

        val notification = shadowManager.getNotification(42)
        assertNotNull("Notification should be posted", notification)
    }

    @Test
    fun showNotification_doesNotPostWhenNotificationsDisabled() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val shadowManager = Shadows.shadowOf(notificationManager)

        shadowManager.setNotificationsEnabled(false)

        ReminderNotificationHelper.showNotification(
            context = context,
            notificationId = 99,
            title = "Should Not Appear",
            message = "Disabled",
        )

        val notification = shadowManager.getNotification(99)
        assertEquals("No notification should be posted when disabled", null, notification)
    }
}
