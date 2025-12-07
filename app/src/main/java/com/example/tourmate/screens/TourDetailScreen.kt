package com.example.tourmate.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tourmate.TourViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourDetailScreen(
    navController: NavController,
    tourId: Int,
    viewModel: TourViewModel = viewModel()
) {
    // Get tour by ID
    val tour by viewModel.getTourById(tourId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            // Top app bar
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tour Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    // Back button
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
        if (tour == null) {
            // Loading indicator
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Show tour details
            val tourData = tour!!

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tour name
                Text(
                    text = tourData.name,
                    style = MaterialTheme.typography.headlineMedium
                )

                Divider()

                // Tour date
                Text(
                    text = "Date: ${tourData.date}",
                    style = MaterialTheme.typography.bodyLarge
                )

                // Tour time
                Text(
                    text = "Time: ${tourData.time}",
                    style = MaterialTheme.typography.bodyLarge
                )

                // Group info
                Text(
                    text = "Group: ${tourData.groupSize} ${tourData.groupType}",
                    style = MaterialTheme.typography.bodyLarge
                )

                // Show notes if available
                if (tourData.notes.isNotBlank()) {
                    Text(
                        text = "Notes:\n${tourData.notes}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Push content up
                Spacer(modifier = Modifier.weight(1f))

                // View Route button
                Button(
                    onClick = {
                        val encodedTourName = Uri.encode(tourData.name)
                        navController.navigate("map?tourNames=$encodedTourName")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Route")
                }
            }
        }
    }
}