package com.hisabmate.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hisabmate.data.OnboardingManager
import com.hisabmate.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val onboardingManager = OnboardingManager(application)
    
    var startDestination by mutableStateOf(Screen.Onboarding.route)
        private set

    var isLoading by mutableStateOf(true)
        private set

    init {
        onboardingManager.isOnboardingCompleted.onEach { completed ->
            startDestination = if (completed) Screen.Home.route else Screen.Onboarding.route
            isLoading = false
        }.launchIn(viewModelScope)
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            onboardingManager.saveOnboardingCompletion()
        }
    }
}
