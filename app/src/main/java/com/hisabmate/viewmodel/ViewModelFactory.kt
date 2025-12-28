package com.hisabmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hisabmate.data.repository.HisabMateRepository

class HisabMateViewModelFactory(
    private val repository: HisabMateRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(AddRecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddRecordViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(SummaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SummaryViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(StreaksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StreaksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
