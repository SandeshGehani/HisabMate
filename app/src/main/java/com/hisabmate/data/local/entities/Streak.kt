package com.hisabmate.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaks")
data class Streak(
    @PrimaryKey
    val id: Int = 1, // Singleton
    
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastRecordedDate: Long = 0L
)
