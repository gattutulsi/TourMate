package com.example.tourmate.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tourmate.TourViewModel
import com.example.tourmate.notifications.NotificationHelper
import com.example.tourmate.repo.Tour

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTourScreen(
    navController: NavController,
    viewModel: TourViewModel = viewModel()
) {
    // State variables
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var groupSize by remember { mutableStateOf("") }
    var groupType by remember { mutableStateOf("Adult") }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Scaffold with top app bar
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add Tour",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        // Main layout
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tour location input field
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tour Location") },
                placeholder = { Text("e.g. London, Manchester") },
                modifier = Modifier.fillMaxWidth()
            )

            // Tour date
            TextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (e.g. Year-Month-Date)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Tour time
            TextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Time (e.g. 12:00)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Group size
            TextField(
                value = groupSize,
                onValueChange = { groupSize = it },
                label = { Text("Group Size") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Group type dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = groupType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Group Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                // Group type options
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("Adult", "Kids", "Mixed").forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                groupType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Notes input field
            TextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons: ViewMap and Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Show location on map
                Button(
                    onClick = {
                        val trimmed = name.trim()
                        if (trimmed.isNotBlank()) {
                            val encoded = trimmed.replace(" ", "+")
                            navController.navigate("map?tourNames=$encoded")
                        } else {
                            Toast.makeText(
                                context,
                                "Enter at least one valid tour name",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text("Show on Map")
                }

                // Save tour and notifications
                Button(
                    onClick = {
                        viewModel.insertTour(
                            Tour(
                                name = name,
                                date = date,
                                time = time,
                                groupSize = groupSize.toIntOrNull() ?: 0,
                                groupType = groupType,
                                notes = notes,
                                route = ""
                            )
                        )

                        NotificationHelper.showTourAddedNotification(
                            context = context,
                            tourName = name,
                            tourDate = date
                        )

                        navController.popBackStack()
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}