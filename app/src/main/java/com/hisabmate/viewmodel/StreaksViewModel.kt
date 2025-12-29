package com.hisabmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisabmate.data.local.entities.Streak
import com.hisabmate.data.repository.HisabMateRepository
import com.hisabmate.utils.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class StreaksViewModel(private val repository: HisabMateRepository) : ViewModel() {
    
    val streak: StateFlow<Streak?> = repository.getStreak()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Streak())

    private val currentMonth = LocalDate.now().monthValue
    private val currentYear = LocalDate.now().year
    
    val currentMonthEntries = repository.getRecordsForRange(
        DateUtils.getStartOfMonth(currentMonth, currentYear),
        DateUtils.getEndOfMonth(currentMonth, currentYear)
    ).map { it.size }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    
    val earnedBadges = repository.getEarnedBadges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    val totalEntries = repository.getTotalRecordsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    
    val consistency = currentMonthEntries.map { entries ->
        val daysInMonthPassed = LocalDate.now().dayOfMonth
        if (daysInMonthPassed > 0) (entries * 100) / daysInMonthPassed else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val last7DaysActivity: StateFlow<List<Boolean>> = flow {
         val today = LocalDate.now()
         val timestamps = (0..6).map { i ->
             today.minusDays(i.toLong()).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
         }.reversed()
         
         repository.getRecordsForRange(timestamps.first(), timestamps.last()).collect { records ->
             val recordDates = records.map {
                 LocalDate.ofInstant(java.time.Instant.ofEpochMilli(it.date), java.time.ZoneId.systemDefault())
             }.toSet()
             
             val activity = (0..6).map { i ->
                 recordDates.contains(today.minusDays(i.toLong()))
             }.reversed()
             emit(activity)
         }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), List(7) { false })
}
