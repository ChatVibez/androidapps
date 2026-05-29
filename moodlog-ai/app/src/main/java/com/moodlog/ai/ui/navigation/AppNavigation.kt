package com.moodlog.ai.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moodlog.ai.R
import com.moodlog.ai.ui.edit.EditEntryScreen
import com.moodlog.ai.ui.history.HistoryScreen
import com.moodlog.ai.ui.home.HomeScreen
import com.moodlog.ai.ui.insights.InsightsScreen
import com.moodlog.ai.ui.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val HISTORY = "history"
    const val INSIGHTS = "insights"
    const val SETTINGS = "settings"
    const val EDIT_ENTRY = "edit/{${NavArgs.ENTRY_ID}}"

    fun editEntry(id: Long) = "edit/$id"
}

object NavArgs {
    const val ENTRY_ID = "entryId"
}

private sealed class TopLevelDestination(
    val route: String,
    val label: Int,
    val icon: ImageVector
) {
    data object Home : TopLevelDestination(Routes.HOME, R.string.tab_home, Icons.Filled.Home)
    data object History : TopLevelDestination(Routes.HISTORY, R.string.tab_history, Icons.Filled.Timeline)
    data object Insights : TopLevelDestination(Routes.INSIGHTS, R.string.tab_insights, Icons.Filled.AutoAwesome)
}

private val topLevelDestinations =
    listOf(TopLevelDestination.Home, TopLevelDestination.History, TopLevelDestination.Insights)

private val topLevelRoutes = topLevelDestinations.map { it.route }.toSet()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route
    val isTopLevel = currentRoute in topLevelRoutes

    Scaffold(
        topBar = {
            if (isTopLevel) {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.action_settings)
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (isTopLevel) {
                AppBottomBar(navController, currentDestination?.hierarchy)
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
                HistoryScreen(
                    contentPadding = padding,
                    onEntryClick = { id -> navController.navigate(Routes.editEntry(id)) }
                )
            }
            composable(TopLevelDestination.Insights.route) {
                InsightsScreen(contentPadding = padding)
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Routes.EDIT_ENTRY,
                arguments = listOf(
                    navArgument(NavArgs.ENTRY_ID) { type = NavType.LongType }
                )
            ) {
                EditEntryScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun AppBottomBar(
    navController: NavHostController,
    hierarchy: Sequence<androidx.navigation.NavDestination>?
) {
    NavigationBar {
        topLevelDestinations.forEach { dest ->
            val selected = hierarchy?.any { it.route == dest.route } == true
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
