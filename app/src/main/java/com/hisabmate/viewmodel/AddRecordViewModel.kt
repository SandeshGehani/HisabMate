package com.hisabmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisabmate.data.local.entities.DailyRecord
import com.hisabmate.data.repository.HisabMateRepository
import com.hisabmate.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddRecordViewModel(private val repository: HisabMateRepository) : ViewModel() {

    private val _existingRecord = MutableStateFlow<DailyRecord?>(null)
    val existingRecord = _existingRecord.asStateFlow()

    fun loadRecordForDate(timestamp: Long) {
        viewModelScope.launch {
            val startOfDay = DateUtils.getStartOfDay(timestamp)
            _existingRecord.value = repository.getRecordByDate(startOfDay)
        }
    }

    fun saveRecord(date: Long, meals: Int, teas: Int, money: Double) {
        viewModelScope.launch {
            val startOfDay = DateUtils.getStartOfDay(date)
            // Preserve creation time if updating
            val createdAt = _existingRecord.value?.createdAt ?: System.currentTimeMillis()
            
            val record = DailyRecord(
                date = startOfDay,
                mealsCount = meals,
                teasCount = teas,
                moneyAmount = money,
                createdAt = createdAt,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveRecord(record)
            repository.refreshStreak(startOfDay)
        }
    }
}
