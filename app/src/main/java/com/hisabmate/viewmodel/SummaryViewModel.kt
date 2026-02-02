package com.hisabmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisabmate.data.local.entities.MonthlySummary
import com.hisabmate.data.repository.HisabMateRepository
import com.hisabmate.utils.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.hisabmate.data.local.entities.DailyRecord
import java.time.LocalDate

class SummaryViewModel(private val repository: HisabMateRepository) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(LocalDate.now().monthValue)
    val selectedMonth = _selectedMonth.asStateFlow()
    
    private val _selectedYear = MutableStateFlow(LocalDate.now().year)
    val selectedYear = _selectedYear.asStateFlow()
    
    // Inputs
    val mealRate = repository.defaultMealRate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 50.0)
    
    val teaRate = repository.defaultTeaRate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10.0)
    
    val monthlyGoal = repository.monthlyGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10000.0)
        
    val rentAmount = repository.rentAmount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    // Data
    val monthRecords: Flow<List<DailyRecord>> = combine(_selectedMonth, _selectedYear) { month, year ->
        DateUtils.getStartOfMonth(month, year) to DateUtils.getEndOfMonth(month, year)
    }.flatMapLatest { (start, end) ->
        repository.getRecordsForRange(start, end)
    }
    
    val totalMeals = monthRecords.map { it.sumOf { r -> r.mealsCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)
        
    val totalTeas = monthRecords.map { it.sumOf { r -> r.teasCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    val mealCost = combine(totalMeals, mealRate) { count: Double, rate: Double -> count * rate }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)
        
    val teaCost = combine(totalTeas, teaRate) { count: Double, rate: Double -> count * rate }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)
        
    val finalAmount = combine(mealCost, teaCost) { m: Double, t: Double -> m + t }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)

    fun updateMonth(month: Int, year: Int) {
        _selectedMonth.value = month
        _selectedYear.value = year
    }

    fun updateMealRate(rate: Double) {
        viewModelScope.launch { repository.updateDefaultRates(meal = rate, tea = teaRate.value) }
    }

    fun updateTeaRate(rate: Double) {
        viewModelScope.launch { repository.updateDefaultRates(meal = mealRate.value, tea = rate) }
    }

    fun updateMonthlyGoal(goal: Double) {
        viewModelScope.launch { repository.updateMonthlyGoal(goal) }
    }

    fun updateRentAmount(amount: Double) {
        viewModelScope.launch { repository.updateRentAmount(amount) }
    }
    
    fun saveSummary() {
        viewModelScope.launch {
            val meals = totalMeals.value
            val teas = totalTeas.value
            val total = finalAmount.value
            
            // Basic badge logic
            val badgeText = when {
                meals > 60 -> "MEAL_KING"
                teas > 100 -> "TEA_MASTER"
                total < 5000 -> "SAVER"
                else -> "REGULAR"
            }
            
            val summary = MonthlySummary(
                month = _selectedMonth.value,
                year = _selectedYear.value,
                totalMeals = meals,
                totalTeas = teas,
                totalMoney = 0.0, 
                pricePerMeal = mealRate.value,
                pricePerTea = teaRate.value,
                finalAmount = total,
                badgeEarned = badgeText
            )
            repository.saveSummary(summary)
        }
    }
}
