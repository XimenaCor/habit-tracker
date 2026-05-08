package com.habittracker.app.data.repository
import com.habittracker.app.data.model.Habit

class HabitRepository {
    private val habits = mutableListOf<Habit>()

    fun addHabit(habit: Habit) {
        habits.add(habit)
    }

    fun getHabits(): List<Habit> {
        return habits.toList()
    }

}