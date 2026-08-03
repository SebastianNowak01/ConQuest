package com.maeldev.conquest.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.maeldev.conquest.ConQuestApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val database = (context.applicationContext as ConQuestApplication).database
            CoroutineScope(Dispatchers.IO).launch {
                ReminderScheduler.rescheduleAllReminders(context, database)
            }
        }
    }
}
