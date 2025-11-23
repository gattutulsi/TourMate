package com.example.tourmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.tourmate.ui.theme.TourMateTheme

class MainActivity : ComponentActivity() {

    // Shared ViewModels
    private val tourViewModel by viewModels<TourViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TourMateTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        tourViewModel = tourViewModel,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}