package com.example.tourmate.data

import androidx.room.*
import com.example.tourmate.repo.Tour
import kotlinx.coroutines.flow.Flow

@Dao
interface TourDao {

    // Get all tours sorted by date (ascending)
    @Query("SELECT * FROM tours ORDER BY date ASC")
    fun getAllTours(): Flow<List<Tour>>

    // Insert a new tour or replace if it already exists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tour: Tour)

    // Update an existing tour
    @Update
    suspend fun update(tour: Tour)

    // Delete a specific tour
    @Delete
    suspend fun delete(tour: Tour)

    // Get a tour by ID
    @Query("SELECT * FROM tours WHERE id = :id LIMIT 1")
    fun getTourById(id: Int): Flow<Tour?>

    // Delete all tours
    @Query("DELETE FROM tours")
    suspend fun deleteAll()
}