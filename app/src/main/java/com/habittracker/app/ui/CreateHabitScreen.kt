package com.habittracker.app.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habittracker.app.data.model.Habit
import com.habittracker.app.data.model.TimeBlock
import java.util.UUID
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.habittracker.app.ui.notifications.HabitScheduler
import com.habittracker.app.ui.notifications.NotificationHelper

@Composable
fun CreateHabitScreen(viewModel: HabitViewModel, navController: NavController) {

    var name by remember { mutableStateOf("") }
    var min_per_week by remember { mutableStateOf(0) }
    var max_per_week by remember { mutableStateOf(0) }
    var time_block by remember { mutableStateOf(TimeBlock.MORNING) }
    var target_time_minutes by remember { mutableStateOf<Int?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        NotificationHelper.createNotificationChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del hábito") }
        )
        TextField(
            value = min_per_week.toString(),
            onValueChange = { min_per_week = it.toIntOrNull() ?: 0 },
            label = { Text("Mínimo de hábitos por semana") }
        )
        TextField(
            value = max_per_week.toString(),
            onValueChange = { max_per_week = it.toIntOrNull() ?: 0 },
            label = { Text("Máximo de hábitos por semana") }
        )
        Row {
            if (time_block == TimeBlock.MORNING) {
                Button(onClick = { time_block = TimeBlock.MORNING }) {
                    Text("Mañana")
                }
            } else {
                OutlinedButton(onClick = { time_block = TimeBlock.MORNING }) {
                    Text("Mañana")
                }
            }
            if (time_block == TimeBlock.AFTERNOON) {
                Button(onClick = { time_block = TimeBlock.AFTERNOON }) {
                    Text("Tarde")
                }
            } else {
                OutlinedButton(onClick = { time_block = TimeBlock.AFTERNOON }) {
                    Text("Tarde")
                }
            }
            if (time_block == TimeBlock.NIGHT) {
                Button(onClick = { time_block = TimeBlock.NIGHT }) {
                    Text("Noche")
                }
            } else {
                OutlinedButton(onClick = { time_block = TimeBlock.NIGHT }) {
                    Text("Noche")
                }
            }
        }
        TextField(
            value = target_time_minutes?.toString() ?: "",
            onValueChange = { target_time_minutes = it.toIntOrNull() },
            label = { Text("Hora del dia objetivo") }
        )
        // Prueba para las notificaciones
        Button(onClick = {
            HabitScheduler.scheduleNotifications(context, viewModel.habits.value)
            NotificationHelper.sendNotification(
                context = context,
                title = "Hábitos pendientes",
                message = "Tienes ${viewModel.habits.value.filter {
                    it.time_block == TimeBlock.MORNING }.size} hábitos esta mañana",
                notificationId = 0
            )
        }) {
            Text("Probar notificación")
        }
        Button(onClick = {
            val habit = Habit(
                habit_id = UUID.randomUUID().toString(),
                user_id = FirebaseAuth.getInstance().currentUser?.uid ?:"",
                name = name,
                min_per_week = min_per_week,
                max_per_week = max_per_week,
                time_block = time_block,
                target_time_minutes = target_time_minutes,
                created_at = System.currentTimeMillis().toString()
            )

            viewModel.addHabit(habit, context)
            navController.navigate("homepage")
        }) {
            Text("Crear Habito")
        }
    }
}

