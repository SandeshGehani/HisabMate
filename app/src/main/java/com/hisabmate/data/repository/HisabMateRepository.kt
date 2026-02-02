package com.hisabmate.data.repository

import com.hisabmate.data.local.dao.DailyRecordDao
import com.hisabmate.data.local.dao.MonthlySummaryDao
import com.hisabmate.data.local.dao.StreakDao
import com.hisabmate.data.local.entities.DailyRecord
import com.hisabmate.data.local.entities.MonthlySummary
import com.hisabmate.data.local.entities.Streak
import com.hisabmate.data.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class HisabMateRepository(
    private val dailyRecordDao: DailyRecordDao,
    private val monthlySummaryDao: MonthlySummaryDao,
    private val streakDao: StreakDao,
    private val userPreferences: UserPreferences
) {
    // User Preferences
    val monthlyGoal = userPreferences.monthlyGoal
    val rentAmount = userPreferences.rentAmount
    val defaultMealRate = userPreferences.defaultMealRate
    val defaultTeaRate = userPreferences.defaultTeaRate
    val shieldCount = userPreferences.shieldCount
    val xpPoints = userPreferences.xpPoints
    
    suspend fun updateMonthlyGoal(goal: Double) = userPreferences.updateMonthlyGoal(goal)
    suspend fun updateRentAmount(amount: Double) = userPreferences.updateRentAmount(amount)
    suspend fun updateDefaultRates(meal: Double, tea: Double) = userPreferences.updateDefaultRates(meal, tea)
    suspend fun updateShieldCount(count: Int) = userPreferences.updateShieldCount(count)
    suspend fun addXp(points: Int) = userPreferences.addXp(points)

    // Daily Records
    suspend fun getRecordByDate(date: Long): DailyRecord? = dailyRecordDao.getRecordByDate(date)
    
    fun getRecordsForRange(start: Long, end: Long): Flow<List<DailyRecord>> = 
        dailyRecordDao.getRecordsForRange(start, end)
        
    suspend fun saveRecord(record: DailyRecord) = dailyRecordDao.insertOrUpdateRecord(record)

    fun getTotalRecordsCount(): Flow<Int> = dailyRecordDao.getTotalRecordsCount()
    
    // Monthly Summary
    suspend fun saveSummary(summary: MonthlySummary) = monthlySummaryDao.insertOrUpdateSummary(summary)
    
    fun getMonthlySummary(month: Int, year: Int): Flow<MonthlySummary?> = monthlySummaryDao.getSummary(month, year)
    
    fun getEarnedBadges(): Flow<List<MonthlySummary>> = monthlySummaryDao.getAllEarnedBadges()
    
    // Streaks
    fun getStreak(): Flow<Streak?> = streakDao.getStreak()
    
    suspend fun updateStreak(streak: Streak) = streakDao.updateStreak(streak)

    suspend fun refreshStreak(newDateMillis: Long) {
        if (newDateMillis > System.currentTimeMillis()) return
        
        val currentStreakObj = streakDao.getStreak().firstOrNull() ?: Streak()
        val lastDateMillis = currentStreakObj.lastRecordedDate
        
        if (lastDateMillis == 0L) {
            // First record ever
            streakDao.updateStreak(currentStreakObj.copy(currentStreak = 1, bestStreak = 1, lastRecordedDate = newDateMillis))
            return
        }

        val lastLocalDate = Instant.ofEpochMilli(lastDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val newLocalDate = Instant.ofEpochMilli(newDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        
        val daysDiff = ChronoUnit.DAYS.between(lastLocalDate, newLocalDate)
        
        if (daysDiff <= 0) return // Already logged today or earlier

        var newStreakCount = currentStreakObj.currentStreak
        val currentShields = userPreferences.shieldCount.first()

        if (daysDiff == 1L) {
            // Consecutive day
            newStreakCount += 1
            
            // Award shield every 7 days (max 3)
            if (newStreakCount % 7 == 0 && currentShields < 3) {
                userPreferences.updateShieldCount(currentShields + 1)
            }
        } else {
            // Gap detected!
            if (currentShields > 0) {
                // Consume shield
                userPreferences.updateShieldCount(currentShields - 1)
                // Preserve streak (act as if it was consecutive)
                newStreakCount += 1 
            } else {
                // Streak broken
                newStreakCount = 1
            }
        }
        
        val best = maxOf(currentStreakObj.bestStreak, newStreakCount)
        
        streakDao.updateStreak(
            currentStreakObj.copy(
                currentStreak = newStreakCount,
                bestStreak = best,
                lastRecordedDate = newDateMillis
            )
        )
    }
}
