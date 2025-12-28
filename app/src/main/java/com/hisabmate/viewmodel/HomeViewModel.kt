package com.hisabmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisabmate.data.local.entities.DailyRecord
import com.hisabmate.data.repository.HisabMateRepository
import com.hisabmate.utils.DateUtils
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class HomeViewModel(private val repository: HisabMateRepository) : ViewModel() {
    
    private val currentMonth = LocalDate.now().monthValue
    private val currentYear = LocalDate.now().year
    
    private val startOfMonth = DateUtils.getStartOfMonth(currentMonth, currentYear)
    private val endOfMonth = DateUtils.getEndOfMonth(currentMonth, currentYear)
    
    val currentMonthRecords: Flow<List<DailyRecord>> = repository.getRecordsForRange(startOfMonth, endOfMonth)
    
    val totalMeals = currentMonthRecords.map { records -> records.sumOf { it.mealsCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
        
    val totalTeas = currentMonthRecords.map { records -> records.sumOf { it.teasCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
        
    val totalContribution = currentMonthRecords.map { records -> records.sumOf { it.moneyAmount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
        
    val todaysRecord = currentMonthRecords.map { records -> 
        val todayStart = DateUtils.getStartOfDay(System.currentTimeMillis())
        records.find { it.date == todayStart }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
