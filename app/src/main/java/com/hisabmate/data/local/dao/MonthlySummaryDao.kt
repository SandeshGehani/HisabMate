package com.hisabmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hisabmate.data.local.entities.MonthlySummary
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlySummaryDao {
    @Query("SELECT * FROM monthly_summaries WHERE month = :month AND year = :year")
    fun getSummary(month: Int, year: Int): Flow<MonthlySummary?>
    
    @Query("SELECT * FROM monthly_summaries WHERE badgeEarned != 'NONE' ORDER BY year DESC, month DESC")
    fun getAllEarnedBadges(): Flow<List<MonthlySummary>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSummary(summary: MonthlySummary)
}
