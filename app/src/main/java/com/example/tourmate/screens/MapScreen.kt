package com.example.tourmate.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, tourNames: List<String>) {
    val context = LocalContext.current
    val fallbackLatLng = LatLng(18.3274, 82.8777) // Default fallback location

    val cameraPositionState = rememberCameraPositionState()

    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }
    var destinationLatLngList by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var locationPermissionGranted by remember { mutableStateOf(false) }

    // Permission request launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Request location permission
    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Load tour destinations
    LaunchedEffect(locationPermissionGranted, tourNames) {
        if (locationPermissionGranted) {
            isLoading = true
            try {
                val destinations = mutableListOf<LatLng>()
                for (name in tourNames) {
                    val loc = getLocationFromName(context, name)
                    if (loc != null) destinations.add(loc)
                }
                if (destinations.isEmpty()) {
                    destinations.add(fallbackLatLng)
                }

                val current = getCurrentLocation(context) ?: fallbackLatLng

                destinationLatLngList = destinations
                currentLatLng = current

                // Move camera to 1st destination
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(destinations.first(), 10f)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // Scaffold layout with topappbar
    Scaffold(
        topBar = {
            // Top bar with back
            TopAppBar(
                title = { Text("Tour Route Map") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        // Main column layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Display tour names
            Text(
                text = "Tour Names: ${tourNames.joinToString()}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            // Conditional content display
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                !locationPermissionGranted -> {
                    // Permission denied message
                    Text(
                        text = "Location permission is required to show the map.",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    // Show Google Map
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = true)
                    ) {
                        // Current user marker
                        currentLatLng?.let {
                            Marker(
                                state = MarkerState(position = it),
                                title = "You are here"
                            )
                        }

                        // Destination markers
                        destinationLatLngList.forEachIndexed { index, latLng ->
                            Marker(
                                state = MarkerState(position = latLng),
                                title = tourNames.getOrNull(index) ?: "Destination",
                                snippet = "Destination"
                            )
                        }

                        // line for route
                        if (destinationLatLngList.size > 1) {
                            Polyline(
                                points = destinationLatLngList,
                                color = Color.Blue,
                                width = 6f
                            )
                        }

                        // Line from user to first destination
                        if (currentLatLng != null && destinationLatLngList.isNotEmpty()) {
                            Polyline(
                                points = listOf(currentLatLng!!, destinationLatLngList.first()),
                                color = Color.Green,
                                width = 4f
                            )
                        }
                    }
                }
            }
        }
    }
}

// Geocode location name
suspend fun getLocationFromName(context: Context, name: String): LatLng? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val result = geocoder.getFromLocationName(name, 1)
            result?.firstOrNull()?.let {
                LatLng(it.latitude, it.longitude)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

// Get user location
suspend fun getCurrentLocation(context: Context): LatLng? {
    return withContext(Dispatchers.IO) {
        try {
            val fineLocationGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val coarseLocationGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!fineLocationGranted && !coarseLocationGranted) {
                return@withContext null
            }

            val client = LocationServices.getFusedLocationProviderClient(context)
            val location = client.lastLocation.await()
            location?.let { LatLng(it.latitude, it.longitude) }
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}