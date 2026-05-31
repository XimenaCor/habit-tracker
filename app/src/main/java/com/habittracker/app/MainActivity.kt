package com.habittracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.habittracker.app.data.repository.HabitRepository
import com.habittracker.app.ui.CreateHabitScreen
import com.habittracker.app.ui.DailyRegistrationScreen
import com.habittracker.app.ui.HabitViewModel
import com.habittracker.app.ui.HomepageScreen
import com.habittracker.app.ui.LoginScreen
import com.habittracker.app.ui.PhilosophyScreen
import com.habittracker.app.ui.SettingsScreen
import com.habittracker.app.ui.theme.HabitTrackerTheme
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.habittracker.app.data.workers.NightlyWorker
import java.util.concurrent.TimeUnit
import androidx.work.OneTimeWorkRequestBuilder

class MainActivity : ComponentActivity() {
    private fun scheduleNightlyWorker() {
        // Calcula cuántos milisegundos faltan para la medianoche
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
            add(java.util.Calendar.DAY_OF_YEAR, 1) // siempre la próxima medianoche
        }
        val delay = calendar.timeInMillis - now

        val nightlyRequest = PeriodicWorkRequestBuilder<NightlyWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "nightly_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            nightlyRequest
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleNightlyWorker()
        // TODO: quitar después de probar
        /*val testRequest = OneTimeWorkRequestBuilder<NightlyWorker>().build()
        WorkManager.getInstance(this).enqueue(testRequest)
        */
        setContent {
            HabitTrackerTheme {
                val navController = rememberNavController()
                val viewModel: HabitViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { HabitViewModel(HabitRepository()) }
                    }
                )
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(navController = navController)
                    }
                    composable("homepage") {
                        HomepageScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                    composable("create_habit") {
                        CreateHabitScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                    composable("daily_registration") {
                        DailyRegistrationScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                    composable("philosophy") {
                        PhilosophyScreen(
                            navController = navController
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}