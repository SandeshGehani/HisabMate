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
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000L) - 1 // End of day in millis
        
        // Get records for today
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
