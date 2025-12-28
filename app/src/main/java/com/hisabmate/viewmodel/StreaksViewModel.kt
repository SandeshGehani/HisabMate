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
    
    // Total entries (mocking for now as repo doesn't have countAll)
    val totalEntries = MutableStateFlow(145) 
    
    // Consistency (mock)
    val consistency = MutableStateFlow(92)
}
