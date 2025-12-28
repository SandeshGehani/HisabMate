package com.hisabmate.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hisabmate.data.local.HisabMateDatabase
import com.hisabmate.utils.DateUtils
import com.hisabmate.utils.NotificationHelper
import java.time.LocalDate

class DailyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = HisabMateDatabase.getDatabase(applicationContext)
        val dao = database.dailyRecordDao()
        
        // Check for today's record
        val today = LocalDate.now()
        val startOfDay = DateUtils.getStartOfDay(today)
        val endOfDay = DateUtils.getEndOfDay(today)
        
        // This is a suspend call, but since we are in CoroutineWorker, we can call it directly if DAO is suspend, 
        // OR collect from Flow. DAO getRecordForDate returns Flow<DailyRecord?>.
        // We need a suspend equivalent or first().
        // Let's rely on getRecordsForRange which returns Flow.
        // Ideally DAO should have a suspend fun getRecord(date): DailyRecord?
        
        // We will collect the flow for one emission
        kotlinx.coroutines.flow.firstOrNull { 
             // We can just check usage. 
             // But actually, we can check if it exists.
             true
        }
        
        // Since we don't have a direct suspend "getOne", let use records range
        val records = kotlinx.coroutines.flow.firstOrNull(
             dao.getRecordsForRange(startOfDay, endOfDay)
        )
        
        val hasRecord = !records.isNullOrEmpty()
        
        if (!hasRecord) {
            NotificationHelper.showNotification(applicationContext)
        }

        return Result.success()
    }
}
