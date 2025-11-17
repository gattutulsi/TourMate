package com.example.tourmate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tourmate.repo.Tour

// Define Room database with Tour entity
@Database(entities = [Tour::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Abstract method to access DAO
    abstract fun tourDao(): TourDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tourmate_database"  // Database name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}