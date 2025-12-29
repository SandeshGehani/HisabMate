package com.hisabmate.data.repository

import com.hisabmate.data.local.dao.DailyRecordDao
import com.hisabmate.data.local.dao.MonthlySummaryDao
import com.hisabmate.data.local.dao.StreakDao
import com.hisabmate.data.local.entities.DailyRecord
import com.hisabmate.data.local.entities.MonthlySummary
import com.hisabmate.data.local.entities.Streak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class HisabMateRepository(
    private val dailyRecordDao: DailyRecordDao,
    private val monthlySummaryDao: MonthlySummaryDao,
    private val streakDao: StreakDao
) {
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

    suspend fun refreshStreak(newDate: Long) {
        if (newDate > System.currentTimeMillis()) return
        
        val currentStreakObj = streakDao.getStreak().firstOrNull() ?: Streak()
        val lastDate = currentStreakObj.lastRecordedDate
        
        // Simple logic: If newDate is 1 day after lastDate, increment.
        // If gap > 1 day, reset to 1.
        // If same date, do nothing.
        
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val diff = newDate - lastDate
        
        var newStreakCount = currentStreakObj.currentStreak
        
        if (diff > 0) {
            if (diff <= oneDayMillis + 10000) { // Tolerance
                newStreakCount += 1
            } else {
                 // Reset if missed a day (simplified, strictly consecutive)
                 // Note: Real logic should check calendar days, here we assume midnight timestamps
                 if (diff > oneDayMillis * 2) { 
                     newStreakCount = 1 
                 } else {
                     // Gap is just 1 missed day ( > 24h but < 48h approx if purely consecutive logic)
                     // For now, sticking to: if it's strictly next day, inc, else reset if far apart.
                     // A robust impl requires converting to LocalDate.
                     newStreakCount = 1
                 }
            }
        } else {
            // Updating old record or same day, don't change streak count usually
            // Unless we are filling a gap, which is complex. 
            // Minimal MVP: Just set to 1 if 0.
            if (newStreakCount == 0) newStreakCount = 1
        }
        
        val best = maxOf(currentStreakObj.bestStreak, newStreakCount)
        
        streakDao.updateStreak(
            currentStreakObj.copy(
                currentStreak = newStreakCount,
                bestStreak = best,
                lastRecordedDate = if (newDate > lastDate) newDate else lastDate
            )
        )
    }
}
