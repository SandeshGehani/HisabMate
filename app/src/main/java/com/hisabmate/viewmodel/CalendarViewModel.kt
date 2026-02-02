package com.hisabmate.viewmodel

import androidx.lifecycle.ViewModel
import com.hisabmate.data.repository.HisabMateRepository
import com.hisabmate.utils.DateUtils
import java.time.LocalDate

import androidx.lifecycle.viewModelScope
import com.hisabmate.data.local.entities.DailyRecord
import kotlinx.coroutines.flow.*

class CalendarViewModel(private val repository: HisabMateRepository) : ViewModel() {
    
    private val _selectedMonth = MutableStateFlow(LocalDate.now().monthValue)
    val selectedMonth = _selectedMonth.asStateFlow()
    
    private val _selectedYear = MutableStateFlow(LocalDate.now().year)
    val selectedYear = _selectedYear.asStateFlow()
    
    val monthlyRecords: Flow<List<DailyRecord>> = combine(_selectedMonth, _selectedYear) { month, year ->
        val start = DateUtils.getStartOfMonth(month, year)
        val end = DateUtils.getEndOfMonth(month, year)
        start to end
    }.flatMapLatest { (start, end) ->
        repository.getRecordsForRange(start, end)
    }

    fun nextMonth() {
        if (_selectedMonth.value == 12) {
            _selectedMonth.value = 1
            _selectedYear.value += 1
        } else {
            _selectedMonth.value += 1
        }
    }

    fun previousMonth() {
        if (_selectedMonth.value == 1) {
            _selectedMonth.value = 12
            _selectedYear.value -= 1
        } else {
            _selectedMonth.value -= 1
        }
    }
}
