package com.habittracker.app.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HabitReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notificationId", 0)
        val message = intent.getStringExtra("message") ?: "Tienes hábitos pendientes"
        NotificationHelper.sendNotification(context, "Hábitos pendientes", message, notificationId)
    }
}