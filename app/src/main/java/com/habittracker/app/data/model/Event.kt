package com.habittracker.app.data.model

enum class EventStatus {
    DONE, NOT_DONE
}

data class Event(
    val event_id: String = "",
    val user_id: String = "",
    val habit_id: String = "",

    val date: String = "",

    val status: EventStatus = EventStatus.NOT_DONE,

    val matched_time_block: Boolean = false,
    val matched_target_time: Boolean = false,

    val created_at: Long = 0L
)