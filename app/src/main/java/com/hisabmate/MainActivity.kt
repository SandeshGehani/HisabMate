package com.hisabmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.hisabmate.data.local.HisabMateDatabase
import com.hisabmate.data.repository.HisabMateRepository
import com.hisabmate.navigation.HisabMateNavHost
import com.hisabmate.ui.theme.HisabMateTheme
import com.hisabmate.utils.NotificationHelper
import com.hisabmate.viewmodel.HisabMateViewModelFactory
import com.hisabmate.viewmodel.MainViewModel
import com.hisabmate.worker.DailyReminderWorker
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Notification Channel
        NotificationHelper.createNotificationChannel(this)
        
        // Schedule Work
        scheduleDailyReminder()

        // Permissions
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }

        // Database & Repo
        val database = HisabMateDatabase.getDatabase(applicationContext)
        val repository = HisabMateRepository(
            database.dailyRecordDao(),
            database.monthlySummaryDao(),
            database.streakDao()
        )

        val viewModelFactory = HisabMateViewModelFactory(repository)
        val mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            HisabMateTheme(darkTheme = mainViewModel.isDarkMode) {
                val navController = rememberNavController()
                
                if (mainViewModel.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    HisabMateNavHost(
                        navController = navController,
                        startDestination = mainViewModel.startDestination,
                        onUpdateTheme = { mainViewModel.toggleTheme() },
                        onFinishOnboarding = {
                            mainViewModel.completeOnboarding()
                            navController.navigate(com.hisabmate.navigation.Screen.Home.route) {
                                popUpTo(com.hisabmate.navigation.Screen.Onboarding.route) { inclusive = true }
                            }
                        },
                        viewModelFactory = viewModelFactory
                    )
                }
            }
        }
    }
    
    private fun scheduleDailyReminder() {
        val workManager = WorkManager.getInstance(applicationContext)
        
        // Calculate delay to next 9 PM
        val now = LocalDateTime.now()
        var target = now.withHour(21).withMinute(0).withSecond(0).withNano(0)
        
        if (now.isAfter(target)) {
            target = target.plusDays(1)
        }
        
        val delay = Duration.between(now, target).toMillis()
        
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            24, TimeUnit.HOURS
        )
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .build()
        
        workManager.enqueueUniquePeriodicWork(
            "DailyReminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
