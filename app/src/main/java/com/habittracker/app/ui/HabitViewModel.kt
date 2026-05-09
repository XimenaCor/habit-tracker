package com.habittracker.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        viewModelScope.launch {
            _habits.value = repository.getHabits()
        }
    }

    fun addHabit(habit: Habit, context: Context) {
        viewModelScope.launch {
            repository.addHabit(habit)
            _habits.value = repository.getHabits()
            HabitScheduler.scheduleNotifications(context, _habits.value)
        }
    }

}