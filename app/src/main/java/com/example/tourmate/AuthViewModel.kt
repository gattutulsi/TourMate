package com.example.tourmate

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Patterns

class AuthViewModel : ViewModel() {

    // Firebase Authentication instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // To show loading spinner
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // To show login/register error messages
    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    // Login
    fun login(email: String, password: String, navController: NavController) {
        // Validate inputs
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and password are required"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Please enter a valid email address"
            return
        }

        // Start login process
        _isLoading.value = true
        _errorMessage.value = ""

        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                _isLoading.value = false

                if (it.isSuccessful) {
                    // Success Go to Home screen
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    // Failed Show Firebase error
                    _errorMessage.value = it.exception?.message ?: "Login failed"
                }
            }
    }

    // Register
    fun register(email: String, password: String, navController: NavController) {
        // Validate inputs
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and password are required"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Please enter a valid email address"
            return
        }

        // Start registration process
        _isLoading.value = true
        _errorMessage.value = ""

        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                _isLoading.value = false

                if (it.isSuccessful) {
                    // Success Go to Home screen
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                } else {
                    // Failed Show Firebase error
                    _errorMessage.value = it.exception?.message ?: "Registration failed"
                }
            }
    }

    // Logout
    fun logout(navController: NavController) {
        auth.signOut()
        navController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }
    }
}