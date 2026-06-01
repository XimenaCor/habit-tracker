package com.habittracker.app.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.habittracker.app.data.model.Habit
import com.habittracker.app.data.model.TimeBlock
import com.habittracker.app.ui.notifications.NotificationHelper
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(viewModel: HabitViewModel, navController: NavController) {

    var name by remember { mutableStateOf("") }
    var min_per_week by remember { mutableStateOf(0) }
    var max_per_week by remember { mutableStateOf(0) }
    var time_block by remember { mutableStateOf(TimeBlock.MORNING) }
    var target_time_minutes by remember { mutableStateOf<Int?>(null) }
    var motivational_phrase by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

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
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Título
        Text(
            text = "Nuevo hábito",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "La consistencia es más importante que la perfección.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Nombre
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del hábito") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Frase motivacional
        OutlinedTextField(
            value = motivational_phrase,
            onValueChange = { motivational_phrase = it },
            label = { Text("Motivo Personal (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Frecuencia
        Text(
            text = "Objetivo semanal",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            OutlinedTextField(
                value = min_per_week.toString(),
                onValueChange = { min_per_week = it.toIntOrNull() ?: 0 },
                label = { Text("Mínimo") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = max_per_week.toString(),
                onValueChange = { max_per_week = it.toIntOrNull() ?: 0 },
                label = { Text("Máximo") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bloque de tiempo
        Text(
            text = "Momento del día",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            listOf(
                TimeBlock.MORNING to "Mañana",
                TimeBlock.AFTERNOON to "Tarde",
                TimeBlock.NIGHT to "Noche"
            ).forEach { (block, label) ->
                if (time_block == block) {
                    Button(
                        onClick = { time_block = block },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text(label) }
                } else {
                    OutlinedButton(
                        onClick = { time_block = block },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text(label) }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hora objetivo
        var showTimePicker by remember { mutableStateOf(false) }
        val timePickerState = rememberTimePickerState(
            initialHour = target_time_minutes?.div(60) ?: 8,
            initialMinute = target_time_minutes?.rem(60) ?: 0
        )
        val timeDisplay = target_time_minutes?.let {
            "%02d:%02d".format(it / 60, it % 60)
        } ?: "Sin hora objetivo"

        Text(
            text = "Hora objetivo (opcional)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { showTimePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = timeDisplay, style = MaterialTheme.typography.bodyMedium)
        }

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        target_time_minutes = timePickerState.hour * 60 + timePickerState.minute
                        showTimePicker = false
                    }) { Text("Aceptar") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        target_time_minutes = null
                        showTimePicker = false
                    }) { Text("Sin hora") }
                },
                text = { TimePicker(state = timePickerState) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón guardar
        Button(
            onClick = {
                if (name.isBlank()) {
                    errorMessage = "El nombre del hábito es obligatorio."
                    showError = true
                    return@Button
                }
                if (min_per_week == 0 || max_per_week == 0) {
                    errorMessage = "La frecuencia mínima y máxima deben ser mayores a 0."
                    showError = true
                    return@Button
                }
                if (min_per_week > max_per_week) {
                    errorMessage = "El mínimo no puede ser mayor que el máximo."
                    showError = true
                    return@Button
                }
                showError = false
                val habit = Habit(
                    habit_id = UUID.randomUUID().toString(),
                    user_id = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    name = name,
                    min_per_week = min_per_week,
                    max_per_week = max_per_week,
                    time_block = time_block,
                    target_time_minutes = target_time_minutes,
                    motivational_phrase = motivational_phrase.ifEmpty { null },
                    created_at = System.currentTimeMillis().toString()
                )
                viewModel.addHabit(habit, context)
                navController.navigate("homepage")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Crear hábito")
        }

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}