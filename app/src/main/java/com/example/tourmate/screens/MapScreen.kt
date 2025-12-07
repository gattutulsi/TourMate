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
import com.google.android.gms.maps.model.CameraPosition
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
    val fallbackLatLng = LatLng(18.3274, 82.8777)

    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }
    var destinationLatLngList by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var locationPermissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Request permissions
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

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(fallbackLatLng, 10f)
    }

    // Load destinations & current location
    LaunchedEffect(locationPermissionGranted, tourNames) {
        if (locationPermissionGranted) {
            isLoading = true
            try {
                val destinations = mutableListOf<LatLng>()
                for (name in tourNames) {
                    getLocationFromName(context, name)?.let { destinations.add(it) }
                }
                if (destinations.isEmpty()) destinations.add(fallbackLatLng)

                val current = getCurrentLocation(context) ?: fallbackLatLng

                destinationLatLngList = destinations
                currentLatLng = current

                // Move camera to first destination or current location
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(destinations.firstOrNull() ?: current, 12f)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Tour Names: ${tourNames.joinToString()}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                !locationPermissionGranted -> {
                    Text(
                        text = "Location permission is required to show the map.",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = locationPermissionGranted)
                    ) {
                        // User marker
                        currentLatLng?.let {
                            Marker(state = MarkerState(position = it), title = "You are here")
                        }
                        // Destination markers
                        destinationLatLngList.forEachIndexed { index, latLng ->
                            Marker(
                                state = MarkerState(position = latLng),
                                title = tourNames.getOrNull(index) ?: "Destination"
                            )
                        }
                        // Polyline between destinations
                        if (destinationLatLngList.size > 1) {
                            Polyline(points = destinationLatLngList, color = Color.Blue, width = 6f)
                        }
                        // Line from user to first destination
                        currentLatLng?.let { current ->
                            destinationLatLngList.firstOrNull()?.let { firstDest ->
                                Polyline(points = listOf(current, firstDest), color = Color.Green, width = 4f)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Geocode location name
suspend fun getLocationFromName(context: Context, name: String): LatLng? = withContext(Dispatchers.IO) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocationName(name, 1)?.firstOrNull()?.let {
            LatLng(it.latitude, it.longitude)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Get current location
suspend fun getCurrentLocation(context: Context): LatLng? = withContext(Dispatchers.IO) {
    try {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fineGranted && !coarseGranted) return@withContext null

        val client = LocationServices.getFusedLocationProviderClient(context)
        val location = client.lastLocation.await()
        location?.let { LatLng(it.latitude, it.longitude) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}