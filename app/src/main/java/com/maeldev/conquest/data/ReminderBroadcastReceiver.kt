package com.maeldev.conquest.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "ConQuest Reminder"
        val message = intent.getStringExtra("message") ?: ""
        val notificationId = intent.getIntExtra("notification_id", 0)

        ReminderNotificationHelper.showNotification(
            context = context,
            notificationId = notificationId,
            title = title,
            message = message,
        )
    }
}
