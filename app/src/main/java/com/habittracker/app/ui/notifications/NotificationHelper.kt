package com.habittracker.app.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "habits_channel",
                "Recordatorio de hábitos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context, title: String, message: String, notificationId: Int) {
        val notification = NotificationCompat.Builder(context, "habits_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(notificationId, notification)
    }
}