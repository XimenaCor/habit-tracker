package com.habittracker.app.ui

import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.navigation.NavController
import com.habittracker.app.data.model.Event
import com.habittracker.app.data.model.EventStatus
import com.habittracker.app.data.model.RegistrationStep
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun DailyRegistrationScreen(viewModel: HabitViewModel, navController: NavController) {

    val pendingHabits by viewModel.pendingHabits.collectAsState()
    var currentIndex by remember { mutableStateOf(0) }
    var currentStep by remember { mutableStateOf<RegistrationStep>(RegistrationStep.MainQuestion) }
    var matchedTimeBlock by remember { mutableStateOf(false) }
    var currentHabitId by remember { mutableStateOf("") }

    // Al entrar a la pantalla, reseteamos el draft y recargamos pendientes
    LaunchedEffect(Unit) {
        viewModel.resetDraft()
        viewModel.loadPendingHabits()
    }

    LaunchedEffect(pendingHabits) {
        if (currentHabitId.isEmpty() && pendingHabits.isNotEmpty()) {
            currentHabitId = pendingHabits.first().habit_id
        }
    }

    val currentHabit = pendingHabits.firstOrNull { it.habit_id == currentHabitId }
    if (pendingHabits.isEmpty() || currentHabit == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¡Todo al día!")
        }
        return
    }


    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    // Avanza al siguiente hábito y resetea el paso actual
    fun nextHabit() {
        val currentPos = pendingHabits.indexOfFirst { it.habit_id == currentHabitId }
        val nextHabit = pendingHabits.getOrNull(currentPos + 1)
        if (nextHabit != null) {
            currentHabitId = nextHabit.habit_id
        } else {
            currentHabitId = ""
        }
        currentStep = RegistrationStep.MainQuestion
        matchedTimeBlock = false
        viewModel.resetDraft()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Fondo placeholder hasta implementar imágenes
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )

            // Texto sobre la imagen — alineado abajo
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                currentHabit.motivational_phrase?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = currentHabit.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            // Overlay de pregunta secundaria según el paso actual
            when (currentStep) {
                is RegistrationStep.TimeBlockQuestion -> {
                    // Pregunta sobre bloque de tiempo
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¿Fue durante tu bloque de tiempo preferido?",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                is RegistrationStep.TargetTimeQuestion -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¿Fue cerca de tu hora objetivo?",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                else -> { }
            }
        }

        // Botones de acción — siempre visibles abajo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✖ NO
            IconButton(onClick = {
                when (currentStep) {
                    is RegistrationStep.MainQuestion -> {
                        // Registra NOT_DONE y avanza
                        viewModel.addEvent(Event(
                            event_id = UUID.randomUUID().toString(),
                            user_id = currentHabit.user_id,
                            habit_id = currentHabit.habit_id,
                            date = today,
                            status = EventStatus.NOT_DONE,
                            matched_time_block = null,
                            matched_target_time = null,
                            created_at = System.currentTimeMillis()
                        ))
                        nextHabit()
                    }
                    is RegistrationStep.TimeBlockQuestion -> {
                        // matched_time_block = false → matched_target_time automáticamente false
                        viewModel.updateDraftTimeBlock(false)
                        viewModel.completeDraft(matchedTargetTime = false)
                        nextHabit()
                    }
                    is RegistrationStep.TargetTimeQuestion -> {
                        // matched_target_time = false
                        viewModel.completeDraft(matchedTargetTime = false)
                        nextHabit()
                    }
                    else -> { }
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "No",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            // ⏳ LATER — visualmente secundario
            OutlinedIconButton(onClick = {
                nextHabit()
            }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Luego"
                )
            }

            // ♥ YES
            IconButton(onClick = {
                when (currentStep) {
                    is RegistrationStep.MainQuestion -> {
                        viewModel.startDraft(
                            habitId = currentHabit.habit_id,
                            userId = currentHabit.user_id
                        )
                        currentStep = RegistrationStep.TimeBlockQuestion
                    }
                    is RegistrationStep.TimeBlockQuestion -> {
                        // matched_time_block = true → pregunta siguiente
                        viewModel.updateDraftTimeBlock(true)
                        currentStep = RegistrationStep.TargetTimeQuestion
                    }
                    is RegistrationStep.TargetTimeQuestion -> {
                        // matched_target_time = true → completa el draft
                        viewModel.completeDraft(matchedTargetTime = true)
                        nextHabit()
                    }
                    else -> { }
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Sí",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

    }
}

