package com.hisabmate.data.local.entities

import androidx.room.Entity

@Entity(
    tableName = "monthly_summaries",
    primaryKeys = ["month", "year"]
)
data class MonthlySummary(
    val month: Int, // 1-12
    val year: Int,
    
    val totalMeals: Int = 0,
    val totalTeas: Int = 0,
    val totalMoney: Double = 0.0,
    
    val pricePerMeal: Double = 0.0,
    val pricePerTea: Double = 0.0,
    
    val finalAmount: Double = 0.0,
    
    val badgeEarned: String? = null // e.g. "GOLD", "NONE"
)
