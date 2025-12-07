package com.example.tourmate.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tourmate.TourViewModel
import com.example.tourmate.notifications.NotificationHelper
import com.example.tourmate.repo.Tour
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTourScreen(
    navController: NavController,
    viewModel: TourViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var groupSize by remember { mutableStateOf("") }
    var groupType by remember { mutableStateOf("Adult") }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Date Picker
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val cal = Calendar.getInstance().apply { timeInMillis = millis }

                            val year = cal.get(Calendar.YEAR)
                            val month = cal.get(Calendar.MONTH) + 1
                            val day = cal.get(Calendar.DAY_OF_MONTH)

                            // Date formatting: YYYY-MM-DD
                            date = String.format("%04d-%02d-%02d", year, month, day)
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker
    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)

    fun openTimePicker() {
        TimePickerDialog(
            context,
            { _, h: Int, m: Int ->
                time = String.format("%02d:%02d", h, m)
            },
            hour,
            minute,
            true
        ).show()
    }

    // Top Bar
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Add Tour",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
                    )
                },
                navigationIcon = {
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
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(18.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Location
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tour Location") },
                placeholder = { Text("e.g. London, Paris") },
                modifier = Modifier.fillMaxWidth()
            )

            // Date Picker
            OutlinedTextField(
                value = date,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                readOnly = true,
                label = { Text("Date") },
                placeholder = { Text("Select date") }
            )

            // Time Picker
            OutlinedTextField(
                value = time,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openTimePicker() },
                readOnly = true,
                label = { Text("Time") },
                placeholder = { Text("Select time") }
            )

            // Group Size
            OutlinedTextField(
                value = groupSize,
                onValueChange = { groupSize = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Group Size") },
                modifier = Modifier.fillMaxWidth()
            )

            // Group Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = groupType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    label = { Text("Group Type") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

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

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Show on Map
                Button(
                    onClick = {
                        val trimmed = name.trim()
                        if (trimmed.isNotEmpty()) {
                            val encoded = trimmed.replace(" ", "+")
                            navController.navigate("map?tourNames=$encoded")
                        } else {
                            Toast.makeText(context, "Enter a valid location", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) { Text("Show on Map") }

                // Save Buttton
                Button(
                    onClick = {
                        if (name.isEmpty() || date.isEmpty() || time.isEmpty()) {
                            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

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
                            context,
                            name,
                            date
                        )

                        navController.popBackStack()
                    }
                ) { Text("Save") }
            }
        }
    }
}