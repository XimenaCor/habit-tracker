package com.habittracker.app.ui.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.habittracker.app.data.model.Habit
import com.habittracker.app.data.model.TimeBlock
import java.util.Calendar

object HabitScheduler {

    fun getNotificationHour(timeBlock: TimeBlock): Int {
        return when (timeBlock) {
            TimeBlock.MORNING -> 8
            TimeBlock.AFTERNOON -> 14
            TimeBlock.NIGHT -> 20
        }
    }

    fun scheduleNotifications(context: Context, habits: List<Habit>) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        TimeBlock.entries.forEach { timeBlock ->
            val habitsInBlock = habits.filter { it.time_block == timeBlock }

            if (habitsInBlock.isNotEmpty()) {

                // 1. Aqui construyo el mensaje
                val blockName = when (timeBlock) {
                    TimeBlock.MORNING -> "mañana"
                    TimeBlock.AFTERNOON -> "tarde"
                    TimeBlock.NIGHT -> "noche"
                }
                val message = "Tienes ${habitsInBlock.size} hábitos que mejorar esta $blockName"

                // 2. Construir el momento exacto de la alarma
                val notificationHour = getNotificationHour(timeBlock)
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, notificationHour)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }

                // 3. Construir el Intent hacia HabitReminderReceiver
                val intent = Intent(context, HabitReminderReceiver::class.java).apply {
                    putExtra("message", message)
                    putExtra("notificationId", timeBlock.ordinal)
                }

                // 4. Envolver el Intent en un PendingIntent
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    timeBlock.ordinal,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // 5. Programar la alarma como repetición diaria
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        }
    }
}