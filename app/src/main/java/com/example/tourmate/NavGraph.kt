package com.example.tourmate

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tourmate.screens.HomeScreen
import com.example.tourmate.screens.LoginScreen
import com.example.tourmate.screens.RegisterScreen
import com.example.tourmate.screens.SplashScreen

@Composable
fun NavGraph() {
    val navController: NavHostController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

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
            HomeScreen(navController = navController, viewModel = authViewModel)
        }
    }
}