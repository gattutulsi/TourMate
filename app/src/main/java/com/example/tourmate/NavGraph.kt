package com.example.tourmate

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tourmate.screens.AddTourScreen
import com.example.tourmate.screens.HomeScreen
import com.example.tourmate.screens.LoginScreen
import com.example.tourmate.screens.RegisterScreen
import com.example.tourmate.screens.SplashScreen
import com.example.tourmate.screens.TourHistoryScreen

@Composable
fun NavGraph() {
    val navController: NavHostController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val tourViewModel: TourViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("login") {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }
        composable("register") {
            RegisterScreen(navController = navController, viewModel = authViewModel)
        }
        composable("home") {
            HomeScreen(navController = navController, viewModel = tourViewModel)
        }

        // Add new tour screen
        composable("add_tour") {
            AddTourScreen(navController = navController, viewModel = tourViewModel)
        }

        // Tour history screen
        composable("tour_history") {
            TourHistoryScreen(navController = navController, viewModel = tourViewModel)
        }
    }
}