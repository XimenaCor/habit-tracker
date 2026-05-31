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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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