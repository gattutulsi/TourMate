package com.example.tourmate

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Patterns

class AuthViewModel : ViewModel() {

    // FirebaseAuth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("") // Error message
    val errorMessage = _errorMessage.asStateFlow()

    // Check if user is already logged in
    fun checkLoginStatus(navController: NavController) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Navigate to HomeScreen if logged in
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            // Navigate to LoginScreen if not logged in
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // Login function
    fun login(email: String, password: String, navController: NavController) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and password are required" // Empty field check
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Please enter a valid email address" // Email format check
            return
        }

        _isLoading.value = true
        _errorMessage.value = ""

        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                _isLoading.value = false
                if (it.isSuccessful) {
                    // Navigate to HomeScreen on success
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    _errorMessage.value = it.exception?.message ?: "Login failed"
                }
            }
    }

    // Registration function
    fun register(email: String, password: String, navController: NavController) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and password are required" // Empty field check
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Please enter a valid email address" // Email format check
            return
        }

        _isLoading.value = true // Start loading
        _errorMessage.value = ""

        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                _isLoading.value = false
                if (it.isSuccessful) {
                    // Navigate to HomeScreen on success
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                } else {
                    _errorMessage.value = it.exception?.message ?: "Registration failed" // Show error
                }
            }
    }

    // Logout function
    fun logout(navController: NavController) {
        auth.signOut()
        navController.navigate("login") {
            // Navigate back to login
            popUpTo("home") { inclusive = true }
        }
    }
}