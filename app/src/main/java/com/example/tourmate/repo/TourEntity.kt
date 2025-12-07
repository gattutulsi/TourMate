package com.example.tourmate.repo

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tour table entity
@Entity(tableName = "tours")
data class Tour(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: String,
    val time: String,
    val groupSize: Int,
    val groupType: String,
    val notes: String,
    val route: String
)