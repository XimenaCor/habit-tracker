package com.habittracker.app.data.repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.habittracker.app.data.model.Event
import com.habittracker.app.data.model.Habit
import kotlinx.coroutines.tasks.await

class HabitRepository {
    private val db = FirebaseFirestore.getInstance()

    // Obtiene el userId en cada llamada en vez de al inicializar
    private fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    suspend fun addHabit(habit: Habit) {
        val userId = getUserId()
        if (userId.isEmpty()) return
        db.collection("users")
            .document(userId)
            .collection("habits")
            .document(habit.habit_id)
            .set(habit)
            .await()
    }

    suspend fun getHabits(): List<Habit> {
        val userId = getUserId()
        if (userId.isEmpty()) return emptyList()
        val snapshot = db.collection("users")
            .document(userId)
            .collection("habits")
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Habit::class.java) }
    }

    suspend fun addEvent(event: Event) {
        val userId = getUserId()
        if (userId.isEmpty()) return
        db.collection("users")
            .document(userId)
            .collection("events")
            .document(event.event_id)
            .set(event)
            .await()
    }

    suspend fun getEventsForToday(): List<Event> {
        val userId = getUserId()
        if (userId.isEmpty()) return emptyList()
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            .format(java.util.Date())
        val snapshot = db.collection("users")
            .document(userId)
            .collection("events")
            .whereEqualTo("date", today)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Event::class.java) }
    }
}