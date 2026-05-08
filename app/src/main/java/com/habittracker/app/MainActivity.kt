package com.habittracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.habittracker.app.data.repository.HabitRepository
import com.habittracker.app.ui.CreateHabitScreen
import com.habittracker.app.ui.HabitViewModel
import com.habittracker.app.ui.notifications.NotificationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)
        setContent {
            val viewModel: HabitViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { HabitViewModel(HabitRepository()) }
                }
            )
            CreateHabitScreen(viewModel = viewModel)
        }
    }
}
