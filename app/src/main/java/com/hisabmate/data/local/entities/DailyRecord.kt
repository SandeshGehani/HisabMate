package com.hisabmate.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_records")
data class DailyRecord(
    @PrimaryKey
    val date: Long, // Epoch millis for start of day
    
    val mealsCount: Int = 0,
    val teasCount: Int = 0,
    val moneyAmount: Double = 0.0,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
