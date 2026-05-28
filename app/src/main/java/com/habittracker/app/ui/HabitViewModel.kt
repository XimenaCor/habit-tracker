package com.habittracker.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habittracker.app.data.model.Event
import com.habittracker.app.data.model.EventStatus
import com.habittracker.app.data.model.Habit
import com.habittracker.app.data.repository.HabitRepository
import com.habittracker.app.ui.notifications.HabitScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits

    private val _pendingHabits = MutableStateFlow<List<Habit>>(emptyList())

    // Draft temporal del evento que se está construyendo
    // Se resetea al entrar a DailyRegistrationScreen o al interrumpir
    data class EventDraft(
        val habitId: String,
        val userId: String,
        var matchedTimeBlock: Boolean? = null,
        var matchedTargetTime: Boolean? = null
    )

    private var _currentDraft: EventDraft? = null
    val pendingHabits: StateFlow<List<Habit>> = _pendingHabits

    init {
        viewModelScope.launch {
            _habits.value = repository.getHabits()
            loadPendingHabits()
        }
    }

    fun addHabit(habit: Habit, context: Context) {
        viewModelScope.launch {
            repository.addHabit(habit)
            _habits.value = repository.getHabits()
            HabitScheduler.scheduleNotifications(context, _habits.value)
            loadPendingHabits()
        }
    }

    fun loadPendingHabits() {
        viewModelScope.launch {
            val allHabits = repository.getHabits()
            val todayEvents = repository.getEventsForToday()
            val completedHabitIds = todayEvents.map { it.habit_id }.toSet()
            _pendingHabits.value = allHabits.filter { it.habit_id !in completedHabitIds }
        }
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            repository.addEvent(event)
            loadPendingHabits()
        }
    }

    // Inicia un nuevo draft cuando el usuario responde YES en Card 1
    fun startDraft(habitId: String, userId: String) {
        _currentDraft = EventDraft(habitId = habitId, userId = userId)
    }

    // Actualiza el draft con la respuesta de Card 2
    fun updateDraftTimeBlock(matched: Boolean) {
        _currentDraft?.matchedTimeBlock = matched
    }

    // Construye el Event final y lo guarda en Firestore
    fun completeDraft(matchedTargetTime: Boolean) {
        val draft = _currentDraft ?: return
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())

        val event = Event(
            event_id = java.util.UUID.randomUUID().toString(),
            user_id = draft.userId,
            habit_id = draft.habitId,
            date = today,
            status = EventStatus.DONE,
            matched_time_block = draft.matchedTimeBlock,
            matched_target_time = matchedTargetTime,
            created_at = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.addEvent(event)
            _currentDraft = null // limpiamos el draft al completar
            loadPendingHabits()
        }
    }

    // Resetea el draft al entrar a DailyRegistrationScreen
    // Evita que un draft huérfano de una sesión anterior contamine el registro
    fun resetDraft() {
        _currentDraft = null
    }

}