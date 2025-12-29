package com.hisabmate.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hisabmate.data.local.HisabMateDatabase
import com.hisabmate.utils.NotificationHelper
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId

class DailyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = HisabMateDatabase.getDatabase(applicationContext)
        val dao = database.dailyRecordDao()
        
        // Get today's date as timestamp
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000L) - 1
        
        // Get records for today - use .first() to get the value from Flow
        val records = dao.getRecordsForRange(startOfDay, endOfDay).first()
        
        val hasRecord = records.isNotEmpty()
        
        if (!hasRecord) {
            NotificationHelper.showNotification(applicationContext)
        }

        return Result.success()
    }
}
