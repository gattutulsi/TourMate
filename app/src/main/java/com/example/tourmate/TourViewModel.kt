package com.example.tourmate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourmate.data.AppDatabase
import com.example.tourmate.repo.Tour
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException

class TourViewModel(application: Application) : AndroidViewModel(application) {

    // Reference to the DAO
    private val dao = AppDatabase.getDatabase(application).tourDao()

    // All Tours
    val allTours: Flow<List<Tour>> = dao.getAllTours()

    // Past Tours: tours with date before today
    val pastTours: Flow<List<Tour>> = allTours.map { tours ->
        val today: LocalDate = try {
            LocalDate.now()  // Works on API 24+ with desugaring
        } catch (e: Exception) {
            null
        } ?: return@map emptyList()

        tours.filter { tour ->
            try {
                val tourDate = LocalDate.parse(tour.date)
                tourDate.isBefore(today)
            } catch (e: DateTimeParseException) {
                false
            }
        }
    }

    // Insert new tour
    fun insertTour(tour: Tour) {
        viewModelScope.launch {
            dao.insert(tour)
        }
    }

    // Update existing tour
    fun updateTour(tour: Tour) {
        viewModelScope.launch {
            dao.update(tour)
        }
    }

    // Delete existing tour
    fun deleteTour(tour: Tour) {
        viewModelScope.launch {
            dao.delete(tour)
        }
    }

    // Get tour by ID
    fun getTourById(id: Int): Flow<Tour?> = dao.getTourById(id)
}
