package com.hisabmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hisabmate.data.local.entities.DailyRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyRecordDao {
    @Query("SELECT * FROM daily_records WHERE date = :date")
    suspend fun getRecordByDate(date: Long): DailyRecord?

    @Query("SELECT * FROM daily_records WHERE date >= :start AND date <= :end ORDER BY date ASC")
    fun getRecordsForRange(start: Long, end: Long): Flow<List<DailyRecord>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecord(record: DailyRecord)
    
    @Query("DELETE FROM daily_records WHERE date = :date")
    suspend fun deleteRecord(date: Long)
}
