package com.moodlog.ai.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moodlog.ai.R
import com.moodlog.ai.ui.history.HistoryScreen
import com.moodlog.ai.ui.home.HomeScreen
import com.moodlog.ai.ui.insights.InsightsScreen

private sealed class TopLevelDestination(
    val route: String,
    val label: Int,
    val icon: ImageVector
) {
    data object Home : TopLevelDestination("home", R.string.tab_home, Icons.Filled.Home)
    data object History : TopLevelDestination("history", R.string.tab_history, Icons.Filled.Timeline)
    data object Insights : TopLevelDestination("insights", R.string.tab_insights, Icons.Filled.AutoAwesome)
}

private val topLevelDestinations =
    listOf(TopLevelDestination.Home, TopLevelDestination.History, TopLevelDestination.Insights)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                topLevelDestinations.forEach { dest ->
                    val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = null) },
                        label = { Text(stringResource(dest.label)) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Home.route
        ) {
            composable(TopLevelDestination.Home.route) {
                HomeScreen(contentPadding = padding)
            }
            composable(TopLevelDestination.History.route) {
                HistoryScreen(contentPadding = padding)
            }
            composable(TopLevelDestination.Insights.route) {
                InsightsScreen(contentPadding = padding)
            }
        }
    }
}
