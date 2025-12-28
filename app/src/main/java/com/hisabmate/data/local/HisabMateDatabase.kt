package com.hisabmate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hisabmate.data.local.dao.DailyRecordDao
import com.hisabmate.data.local.dao.MonthlySummaryDao
import com.hisabmate.data.local.dao.StreakDao
import com.hisabmate.data.local.entities.DailyRecord
import com.hisabmate.data.local.entities.MonthlySummary
import com.hisabmate.data.local.entities.Streak

@Database(
    entities = [DailyRecord::class, MonthlySummary::class, Streak::class],
    version = 1,
    exportSchema = false
)
abstract class HisabMateDatabase : RoomDatabase() {
    abstract fun dailyRecordDao(): DailyRecordDao
    abstract fun monthlySummaryDao(): MonthlySummaryDao
    abstract fun streakDao(): StreakDao
    
    companion object {
        @Volatile
        private var INSTANCE: HisabMateDatabase? = null
        
        fun getDatabase(context: Context): HisabMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HisabMateDatabase::class.java,
                    "hisabmate_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
