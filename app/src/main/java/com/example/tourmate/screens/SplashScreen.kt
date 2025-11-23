package com.example.tourmate.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tourmate.AuthViewModel
import com.example.tourmate.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // Check login status when splash screen is displayed
    LaunchedEffect(key1 = true) {
        // Optional: show splash for 2 seconds
        delay(2000)
        authViewModel.checkLoginStatus(navController)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // TourMate logo image
        Image(
            painter = painterResource(id = R.drawable.tourmate_logo),
            contentDescription = "Tour Mate Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}