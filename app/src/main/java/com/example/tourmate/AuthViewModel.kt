package com.example.tourmate

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Patterns

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    // Check if user is already logged in
    fun checkLoginStatus(navController: NavController) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User already logged in → go to HomeScreen
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            // User not logged in → go to LoginScreen
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    fun login(email: String, password: String, navController: NavController) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and password are required"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Please enter a valid email address"
            return
        }

        _isLoading.value = true
        _errorMessage.value = ""

        auth.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                _isLoading.value = false
                if (it.isSuccessful) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    _errorMessage.value = it.exception?.message ?: "Login failed"
                }
            }
    }

    fun register(email: String, password: String, navController: NavController) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and password are required"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "Please enter a valid email address"
            return
        }

        _isLoading.value = true
        _errorMessage.value = ""

        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener {
                _isLoading.value = false
                if (it.isSuccessful) {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                } else {
                    _errorMessage.value = it.exception?.message ?: "Registration failed"
                }
            }
    }

    fun logout(navController: NavController) {
        auth.signOut()
        navController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }
    }
}