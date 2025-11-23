package com.example.tourmate

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tourmate.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    tourViewModel: TourViewModel,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash Screen
        composable("splash") {
            SplashScreen(navController = navController, authViewModel = authViewModel)
        }

        // Login Screen
        composable("login") {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }

        // Register Screen
        composable("register") {
            RegisterScreen(navController = navController, viewModel = authViewModel)
        }

        // Home Screen
        composable("home") {
            HomeScreen(navController = navController, viewModel = tourViewModel)
        }

        // Add Tour Screen
        composable("add_tour") {
            AddTourScreen(navController = navController, viewModel = tourViewModel)
        }

        // Tour History Screen
        composable("tour_history") {
            TourHistoryScreen(navController = navController, viewModel = tourViewModel)
        }

        // Map Screen with optional tour names
        composable(
            route = "map?tourNames={tourNames}",
            arguments = listOf(
                navArgument("tourNames") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val tourNamesString = backStackEntry.arguments?.getString("tourNames") ?: ""
            val tourNamesList = if (tourNamesString.isNotBlank()) {
                tourNamesString
                    .split(",")
                    .map { it.trim().replace("+", " ") }
                    .filter { it.isNotEmpty() }
            } else {
                emptyList()
            }

            MapScreen(navController = navController, tourNames = tourNamesList)
        }
    }
}