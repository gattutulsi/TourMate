package com.example.tourmate.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tourmate.TourViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourHistoryScreen(
    navController: NavController,
    viewModel: TourViewModel
) {
    // Get past tours
    val pastTours by viewModel.pastTours.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            // App bar
            CenterAlignedTopAppBar(
                title = {
                    // Title text
                    Text(
                        text = "Tour History",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
                    )
                },
                navigationIcon = {
                    // Back button icon
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF00897B),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        // Tour list display
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
        ) {
            items(pastTours) { tour ->
                // Tour card item
                Card(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("tour_detail/${tour.id}")
                        },
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    // Card content
                    ListItem(
                        headlineContent = { Text(tour.name) },
                        supportingContent = {
                            Text(
                                "${tour.date} at ${tour.time}",
                                color = Color.Gray
                            )
                        }
                    )
                }
            }
        }
    }
}