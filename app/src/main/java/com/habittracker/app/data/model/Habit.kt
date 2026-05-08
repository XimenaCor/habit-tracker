package com.habittracker.app.data.model

enum class TimeBlock {
    MORNING, AFTERNOON, NIGHT
}

data class Habit(
    val habit_id: String = "",
    val user_id: String = "",
    val name: String = "",
    val motivational_phrase: String? = null,
    val image_url: String? = null,

    val min_per_week: Int = 0,
    val max_per_week: Int = 0,

    val time_block: TimeBlock = TimeBlock.MORNING,
    val target_time_minutes: Int? = null,

    val created_at: String = ""
)
