package com.habittracker.app.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.habittracker.app.data.model.Event
import com.habittracker.app.data.model.EventStatus
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class NightlyWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.success()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        return try {
            // 1. Obtener todos los hábitos activos
            val habitsSnapshot = db.collection("users")
                .document(userId)
                .collection("habits")
                .get()
                .await()

            // 2. Obtener todos los eventos de hoy
            val eventsSnapshot = db.collection("users")
                .document(userId)
                .collection("events")
                .whereEqualTo("date", today)
                .get()
                .await()

            // 3. Calcular hábitos sin evento hoy
            val habitIdsWithEvent = eventsSnapshot.documents
                .mapNotNull { it.getString("habit_id") }
                .toSet()

            val habitsWithoutEvent = habitsSnapshot.documents
                .filter { it.id !in habitIdsWithEvent }

            // 4. Crear evento NO_DATA para cada hábito sin registro
            habitsWithoutEvent.forEach { habitDoc ->
                val event = Event(
                    event_id = UUID.randomUUID().toString(),
                    user_id = userId,
                    habit_id = habitDoc.id,
                    date = today,
                    status = EventStatus.NO_DATA,
                    matched_time_block = null,
                    matched_target_time = null,
                    created_at = System.currentTimeMillis()
                )
                db.collection("users")
                    .document(userId)
                    .collection("events")
                    .document(event.event_id)
                    .set(event)
                    .await()
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}