package com.moodlog.ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moodlog.ai.ui.home.HomeScreen

object Routes {
    const val HOME = "home"
    const val HISTORY = "history"
    const val INSIGHTS = "insights"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen() }
        // History & Insights screens akan ditambahkan di iterasi berikutnya.
    }
}
