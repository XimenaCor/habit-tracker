package com.habittracker.app.data.repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.habittracker.app.data.model.Habit
import kotlinx.coroutines.tasks.await

class HabitRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    suspend fun addHabit(habit: Habit) {
        db.collection("users")
            .document(userId)
            .collection("habits")
            .document(habit.habit_id)
            .set(habit)
            .await()
    }

    suspend fun getHabits(): List<Habit> {
        val snapshot = db.collection("users")
            .document(userId)
            .collection("habits")
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Habit::class.java) }
    }
}