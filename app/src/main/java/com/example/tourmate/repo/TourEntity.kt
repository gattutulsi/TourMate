package com.example.tourmate.repo

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tour table entity
@Entity(tableName = "tours")
data class Tour(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Unique tour ID
    val name: String,       // Tour title
    val date: String,       // Tour Date (Format: Year-Month-Date)
    val time: String,       // Tour Time (Format: HH:mm)
    val groupSize: Int,     // Number of people
    val groupType: String,
    val notes: String,
    val route: String
)