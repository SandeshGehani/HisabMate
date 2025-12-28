package com.hisabmate.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.hisabmate.ui.screens.*
import com.hisabmate.viewmodel.AddRecordViewModel
import com.hisabmate.viewmodel.CalendarViewModel
import com.hisabmate.viewmodel.HisabMateViewModelFactory
import com.hisabmate.viewmodel.HomeViewModel

@Composable
fun HisabMateNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Onboarding.route,
    onFinishOnboarding: () -> Unit = {},
    viewModelFactory: HisabMateViewModelFactory
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    onFinishOnboarding()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)
            HomeScreen(
                viewModel = viewModel,
                onNavigateToEntry = { 
                    val today = System.currentTimeMillis()
                    navController.navigate(Screen.AddEditRecord.createRoute(today)) 
                },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) }
            )
        }
        composable(
            route = Screen.AddEditRecord.route,
            arguments = listOf(navArgument("dateMillis") { type = NavType.LongType })
        ) { backStackEntry ->
            val dateMillis = backStackEntry.arguments?.getLong("dateMillis") ?: System.currentTimeMillis()
            val viewModel: AddRecordViewModel = viewModel(factory = viewModelFactory)
            AddEditRecordScreen(
                viewModel = viewModel,
                selectedDate = dateMillis,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Calendar.route) {
            val viewModel: CalendarViewModel = viewModel(factory = viewModelFactory)
            CalendarScreen(
                viewModel = viewModel,
                onNavigateToEntry = { dateMillis ->
                    navController.navigate(Screen.AddEditRecord.createRoute(dateMillis))
                }
            )
        }
        composable(Screen.Summary.route) {
            val viewModel: com.hisabmate.viewmodel.SummaryViewModel = viewModel(factory = viewModelFactory)
            SummaryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Streaks.route) {
            val viewModel: com.hisabmate.viewmodel.StreaksViewModel = viewModel(factory = viewModelFactory)
            StreaksScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
