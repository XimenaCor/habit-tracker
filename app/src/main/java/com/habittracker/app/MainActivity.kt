package com.habittracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.habittracker.app.data.repository.HabitRepository
import com.habittracker.app.ui.CreateHabitScreen
import com.habittracker.app.ui.DailyRegistrationScreen
import com.habittracker.app.ui.HabitViewModel
import com.habittracker.app.ui.HomepageScreen
import com.habittracker.app.ui.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                    val viewModel: HabitViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer { HabitViewModel(HabitRepository()) }
                        }
                    )
                    HomepageScreen(navController = navController, viewModel = viewModel)
                }
                composable("create_habit") {
                    val viewModel: HabitViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer { HabitViewModel(HabitRepository()) }
                        }
                    )
                    CreateHabitScreen(navController = navController, viewModel = viewModel)
                }
                composable("daily_registration") {
                    DailyRegistrationScreen()
                }
            }
        }
    }
}