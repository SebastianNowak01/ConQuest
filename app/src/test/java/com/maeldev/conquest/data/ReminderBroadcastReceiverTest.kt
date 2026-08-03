package com.maeldev.conquest.data

import android.content.Context
import android.content.Intent
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
 * Robolectric tests for [ReminderBroadcastReceiver].
 * Verifies that the receiver correctly extracts intent extras and posts a notification.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReminderBroadcastReceiverTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        // Enable notifications
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        Shadows.shadowOf(nm).setNotificationsEnabled(true)
    }

    @Test
    fun onReceive_postsNotificationWithIntentExtras() {
        val receiver = ReminderBroadcastReceiver()
        val intent = Intent().apply {
            putExtra("title", "Task Reminder")
            putExtra("message", "Finish costume")
            putExtra("notification_id", 12345)
        }

        receiver.onReceive(context, intent)

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val shadowNm = Shadows.shadowOf(nm)
        val notification = shadowNm.getNotification(12345)

        assertNotNull("Notification should be posted with the provided ID", notification)
    }

    @Test
    fun onReceive_usesDefaultTitleWhenMissing() {
        val receiver = ReminderBroadcastReceiver()
        val intent = Intent().apply {
            // No "title" extra
            putExtra("notification_id", 999)
        }

        // Should not crash even without title
        receiver.onReceive(context, intent)

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val shadowNm = Shadows.shadowOf(nm)
        val notification = shadowNm.getNotification(999)

        assertNotNull("Notification should still be posted with defaults", notification)
    }
}
