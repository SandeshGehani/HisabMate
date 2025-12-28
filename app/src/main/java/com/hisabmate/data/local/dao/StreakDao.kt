package com.hisabmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hisabmate.data.local.entities.Streak
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM streaks WHERE id = 1")
    fun getStreak(): Flow<Streak?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStreak(streak: Streak)
}
