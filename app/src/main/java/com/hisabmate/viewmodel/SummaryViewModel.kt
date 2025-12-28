package com.hisabmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisabmate.data.local.entities.MonthlySummary
import com.hisabmate.data.repository.HisabMateRepository
import com.hisabmate.utils.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class SummaryViewModel(private val repository: HisabMateRepository) : ViewModel() {

    private val currentMonth = LocalDate.now().monthValue
    private val currentYear = LocalDate.now().year
    
    // Inputs
    private val _mealRate = MutableStateFlow(50.0)
    val mealRate = _mealRate.asStateFlow()
    
    private val _teaRate = MutableStateFlow(10.0)
    val teaRate = _teaRate.asStateFlow()
    
    // Data
    private val monthRecords = repository.getRecordsForRange(
        DateUtils.getStartOfMonth(currentMonth, currentYear),
        DateUtils.getEndOfMonth(currentMonth, currentYear)
    )
    
    val totalMeals = monthRecords.map { it.sumOf { r -> r.mealsCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
        
    val totalTeas = monthRecords.map { it.sumOf { r -> r.teasCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val mealCost = combine(totalMeals, _mealRate) { count, rate -> count * rate }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)
        
    val teaCost = combine(totalTeas, _teaRate) { count, rate -> count * rate }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)
        
    val finalAmount = combine(mealCost, teaCost) { m, t -> m + t }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    fun updateMealRate(rate: Double) {
        _mealRate.value = rate
    }

    fun updateTeaRate(rate: Double) {
        _teaRate.value = rate
    }
    
    fun saveSummary() {
        viewModelScope.launch {
            // Check if all days have records
            val daysInMonth = java.time.YearMonth.of(currentYear, currentMonth).lengthOfMonth()
            val hasAllDays = totalMeals.value >= daysInMonth // Simplified: assumes at least 1 meal per day
            
            val badgeText = if (hasAllDays) "COMPLETE_MONTH" else "NONE"
            
            val summary = MonthlySummary(
                month = currentMonth,
                year = currentYear,
                totalMeals = totalMeals.value,
                totalTeas = totalTeas.value,
                totalMoney = 0.0, // Simplification for now, usually sum of moneyAmount
                pricePerMeal = _mealRate.value,
                pricePerTea = _teaRate.value,
                finalAmount = finalAmount.value,
                badgeEarned = badgeText
            )
            repository.saveSummary(summary)
        }
    }
}
