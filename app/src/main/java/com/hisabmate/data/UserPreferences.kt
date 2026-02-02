package com.hisabmate.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val MONTHLY_GOAL = doublePreferencesKey("monthly_goal")
        val RENT_AMOUNT = doublePreferencesKey("rent_amount")
        val DEFAULT_MEAL_RATE = doublePreferencesKey("default_meal_rate")
        val DEFAULT_TEA_RATE = doublePreferencesKey("default_tea_rate")
    }

    val monthlyGoal: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[MONTHLY_GOAL] ?: 10000.0
    }

    val rentAmount: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[RENT_AMOUNT] ?: 0.0
    }
    
    val defaultMealRate: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_MEAL_RATE] ?: 50.0
    }
    
    val defaultTeaRate: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_TEA_RATE] ?: 10.0
    }

    suspend fun updateMonthlyGoal(goal: Double) {
        context.dataStore.edit { preferences ->
            preferences[MONTHLY_GOAL] = goal
        }
    }

    suspend fun updateRentAmount(amount: Double) {
        context.dataStore.edit { preferences ->
            preferences[RENT_AMOUNT] = amount
        }
    }
    
    suspend fun updateDefaultRates(meal: Double, tea: Double) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_MEAL_RATE] = meal
            preferences[DEFAULT_TEA_RATE] = tea
        }
    }
}
