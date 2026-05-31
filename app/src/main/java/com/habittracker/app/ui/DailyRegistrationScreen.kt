package com.habittracker.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.roundToInt
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    var currentStep by remember { mutableStateOf<RegistrationStep>(RegistrationStep.MainQuestion) }
    var matchedTimeBlock by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val swipeThresholdPx = with(density) { 120.dp.toPx() }
    var currentHabitId by remember { mutableStateOf("") }
    val habits by viewModel.habits.collectAsState()

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

    // Pantalla de todo al día
    if (pendingHabits.isEmpty() || currentHabit == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (habits.isEmpty()) {
                // No tiene hábitos creados
                Text(
                    text = "Sin hábitos",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Aún no tienes hábitos creados. Pulsa + en el inicio para crear el primero.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            } else {
                // Ya registró todo
                Text(
                    text = "Todo al día",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Has registrado todos tus hábitos de hoy.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            TextButton(onClick = { navController.navigate("homepage") }) {
                Text(
                    text = "Volver al inicio",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        return
    }

    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    // Fondo según time_block
    val cardBackground = when (currentStep) {
        is RegistrationStep.MainQuestion -> Color(0xFFEEEDFE) // Purple50
        else -> Color(0xFFCECBF6) // Purple100
    }

    val textColor = Color(0xFF26215C) // Purple900

    fun nextHabit() {
        val currentPos = pendingHabits.indexOfFirst { it.habit_id == currentHabitId }
        val next = pendingHabits.getOrNull(currentPos + 1)
        currentHabitId = next?.habit_id ?: ""
        currentStep = RegistrationStep.MainQuestion
        matchedTimeBlock = false
        offsetX = 0f
        viewModel.resetDraft()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Card principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(currentHabitId) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            when {
                                // Swipe derecho → YES
                                offsetX > swipeThresholdPx -> {
                                    offsetX = 0f
                                    viewModel.startDraft(
                                        habitId = currentHabit.habit_id,
                                        userId = currentHabit.user_id
                                    )
                                    currentStep = RegistrationStep.TimeBlockQuestion
                                }
                                // Swipe izquierdo → NO
                                offsetX < -swipeThresholdPx -> {
                                    offsetX = 0f
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
                                // No llegó al threshold → vuelve al centro
                                else -> offsetX = 0f
                            }
                        },
                        onDragCancel = { offsetX = 0f },
                        onHorizontalDrag = { _, dragAmount ->
                            // Solo permitir swipe en MainQuestion
                            if (currentStep is RegistrationStep.MainQuestion) {
                                offsetX += dragAmount
                            }
                        }
                    )
                }
        ) {
            // Fondo según time_block
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(cardBackground)
            )

            // Overlay x — swipe izquierda
            if (offsetX < 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF09595).copy(alpha = (-offsetX / swipeThresholdPx).coerceIn(0f, 0.5f)))
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color(0xFFA32D2D).copy(alpha = (-offsetX / swipeThresholdPx).coerceIn(0f, 1f)),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(32.dp)
                        .size(48.dp)
                )
            }

            // Overlay corazon — swipe derecha
            if (offsetX > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF9FE1CB).copy(alpha = (offsetX / swipeThresholdPx).coerceIn(0f, 0.5f)))
                )
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFF0F6E56).copy(alpha = (offsetX / swipeThresholdPx).coerceIn(0f, 1f)),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(32.dp)
                        .size(48.dp)
                )
            }

            // Gradiente oscuro en la parte inferior para legibilidad
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            // Frase y nombre — centrados en la card
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (currentStep) {
                    is RegistrationStep.MainQuestion -> {
                        // Frase y nombre del hábito
                        currentHabit.motivational_phrase?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleLarge,
                                color = textColor,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        Text(
                            text = currentHabit.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                    }
                    is RegistrationStep.TimeBlockQuestion -> {
                        Text(
                            text = "¿Fue durante tu bloque de tiempo preferido?",
                            style = MaterialTheme.typography.titleLarge,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                    }
                    is RegistrationStep.TargetTimeQuestion -> {
                        Text(
                            text = "¿Fue cerca de tu hora objetivo?",
                            style = MaterialTheme.typography.titleLarge,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> { }
                }
            }
        }

        // Animación de pulso cuando que aparece para pregunta secundaria
        val isSecondaryQuestion = currentStep is RegistrationStep.TimeBlockQuestion ||
                currentStep is RegistrationStep.TargetTimeQuestion

        val buttonScale by animateFloatAsState(
            targetValue = if (isSecondaryQuestion) 1.15f else 1f,
            animationSpec = tween(durationMillis = 300),
            label = "buttonScale"
        )

        // Botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✖ NO
            IconButton(
                onClick = {
                    when (currentStep) {
                        is RegistrationStep.MainQuestion -> {
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
                            viewModel.updateDraftTimeBlock(false)
                            viewModel.completeDraft(matchedTargetTime = false)
                            nextHabit()
                        }
                        is RegistrationStep.TargetTimeQuestion -> {
                            viewModel.completeDraft(matchedTargetTime = false)
                            nextHabit()
                        }
                        else -> { }
                    }
                },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .graphicsLayer { scaleX = buttonScale; scaleY = buttonScale }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "No",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            }

            // ⏳ LATER
            OutlinedIconButton(
                onClick = { nextHabit() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Luego",
                    modifier = Modifier.size(20.dp)
                )
            }

            // ♥ YES
            IconButton(
                onClick = {
                    when (currentStep) {
                        is RegistrationStep.MainQuestion -> {
                            viewModel.startDraft(
                                habitId = currentHabit.habit_id,
                                userId = currentHabit.user_id
                            )
                            currentStep = RegistrationStep.TimeBlockQuestion
                        }
                        is RegistrationStep.TimeBlockQuestion -> {
                            viewModel.updateDraftTimeBlock(true)
                            currentStep = RegistrationStep.TargetTimeQuestion
                        }
                        is RegistrationStep.TargetTimeQuestion -> {
                            viewModel.completeDraft(matchedTargetTime = true)
                            nextHabit()
                        }
                        else -> { }
                    }
                },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .graphicsLayer { scaleX = buttonScale; scaleY = buttonScale }
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Sí",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}