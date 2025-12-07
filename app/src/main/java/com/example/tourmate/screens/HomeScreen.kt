package com.example.tourmate.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tourmate.AuthViewModel
import com.example.tourmate.TourViewModel
import com.example.tourmate.repo.Tour
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    tourViewModel: TourViewModel,
    authViewModel: AuthViewModel
) {
    // Observe all tours
    val tours by tourViewModel.allTours.collectAsState(initial = emptyList())

    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val now = Date()

    // Filter upcoming tours
    val upcomingTours = tours.filter { tour ->
        try {
            val tourDateTime = formatter.parse("${tour.date} ${tour.time}")
            tourDateTime != null && !tourDateTime.before(now)
        } catch (e: Exception) {
            false
        }
    }

    // State for delete confirmation
    var showDeleteDialog by remember { mutableStateOf(false) }
    var tourToDelete by remember { mutableStateOf<Tour?>(null) }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Tour") },
            text = { Text("Are you sure you want to delete this tour?") },
            confirmButton = {
                TextButton(onClick = {
                    tourToDelete?.let { tourViewModel.deleteTour(it) }
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        // Title
                        text = "TourMate",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                        modifier = Modifier.padding(start = 10.dp)
                    )
                },
                actions = {
                    IconButton(onClick = { authViewModel.logout(navController) }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00897B),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_tour") },
                containerColor = Color(0xFF00897B)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Tour")
            }
        },
        bottomBar = {
            // Bottom nav bar
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") }, // home
                    label = { Text("Home") },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = "History") }, //history
                    label = { Text("History") },
                    selected = false,
                    onClick = { navController.navigate("tour_history") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifications") }, //notifications
                    label = { Text("Notifications") },
                    selected = false,
                    onClick = { navController.navigate("notifications") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
        ) {
            items(upcomingTours) { tour ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { navController.navigate("tour_detail/${tour.id}") }
                        ) {
                            Text(
                                text = tour.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${tour.date} at ${tour.time}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }

                        Row {
                            // Delete button
                            IconButton(onClick = {
                                tourToDelete = tour
                                showDeleteDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Tour",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}