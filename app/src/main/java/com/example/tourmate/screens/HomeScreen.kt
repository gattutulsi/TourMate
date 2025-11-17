package com.example.tourmate.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tourmate.TourViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: TourViewModel
) {
    // Observe all tours
    val tours by viewModel.allTours.collectAsState(initial = emptyList())

    // Format date and get current
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val now = Date()

    // Filter future tours
    val upcomingTours = tours.filter { tour ->
        try {
            val tourDateTime = formatter.parse("${tour.date} ${tour.time}")
            tourDateTime != null && !tourDateTime.before(now)
        } catch (e: Exception) {
            false
        }
    }

    // Scaffold with Topappbar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TourMate", //Topbar title
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                        modifier = Modifier.padding(start = 10.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            // Add tour Button
            FloatingActionButton(
                onClick = { navController.navigate("add_tour") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Tour")
            }
        },
        bottomBar = {
            // Bottom navigation bar
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                // Home selected
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { /* Already on Home */ }
                )
                // Navigate to History
                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History") },
                    selected = false,
                    onClick = { navController.navigate("tour_history") }
                )
                // Navigate to Notifications
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifications") },
                    label = { Text("Notifications") },
                    selected = false,
                    onClick = {
                        navController.navigate("notifications")
                    }
                )
            }
        }
    ) { padding ->
        // Tour list content
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
        ) {
            items(upcomingTours) { tour ->
                // Card for each tour
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("tour_detail/${tour.id}")
                        },
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    // Card content
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Tour name
                        Text(
                            text = tour.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Tour date and time
                        Text(
                            text = "${tour.date} at ${tour.time}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}