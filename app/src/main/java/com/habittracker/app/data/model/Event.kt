package com.habittracker.app.data.model

enum class EventStatus {
    DONE, NOT_DONE, NO_DATA
}

data class Event(
    val event_id: String = "",
    val user_id: String = "",
    val habit_id: String = "",

    val date: String = "",

    val status: EventStatus = EventStatus.NO_DATA,

    val matched_time_block: Boolean? = null,
    val matched_target_time: Boolean? = null,

    val created_at: Long = 0L
)