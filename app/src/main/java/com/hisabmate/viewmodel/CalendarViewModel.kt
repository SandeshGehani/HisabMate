package com.hisabmate.viewmodel

import androidx.lifecycle.ViewModel
import com.hisabmate.data.repository.HisabMateRepository
import com.hisabmate.utils.DateUtils
import java.time.LocalDate

class CalendarViewModel(private val repository: HisabMateRepository) : ViewModel() {
    
    // For now, hardcoded to current month like HomeViewModel
    private val currentMonth = LocalDate.now().monthValue
    private val currentYear = LocalDate.now().year
    
    private val startOfMonth = DateUtils.getStartOfMonth(currentMonth, currentYear)
    private val endOfMonth = DateUtils.getEndOfMonth(currentMonth, currentYear)
    
    val monthlyRecords = repository.getRecordsForRange(startOfMonth, endOfMonth)
}
