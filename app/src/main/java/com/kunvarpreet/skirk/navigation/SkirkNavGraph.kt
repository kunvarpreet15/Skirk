package com.kunvarpreet.skirk.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kunvarpreet.skirk.di.AppContainer
import com.kunvarpreet.skirk.presentation.config.WidgetConfigurationScreen
import com.kunvarpreet.skirk.presentation.dashboard.DashboardScreen
import com.kunvarpreet.skirk.presentation.dashboard.DashboardViewModel
import com.kunvarpreet.skirk.presentation.editor.DashboardEditorScreen
import com.kunvarpreet.skirk.presentation.picker.WidgetPickerScreen
import com.kunvarpreet.skirk.presentation.settings.SettingsScreen

@Composable
fun SkirkNavGraph(
    appContainer: AppContainer,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.StandByDashboard.route,
        modifier = modifier
    ) {
        composable(Screen.StandByDashboard.route) {
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.provideFactory(
                    dashboardRepository = appContainer.dashboardRepository,
                    userSettingsRepository = appContainer.userSettingsRepository
                )
            )

            DashboardScreen(
                viewModel = dashboardViewModel,
                widgetRegistry = appContainer.widgetRegistry,
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToEditor = {
                    navController.navigate(Screen.DashboardEditor.route)
                },
                onLaunchStandBy = {
                    appContainer.standByController.enterStandByManually()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                userSettingsRepository = appContainer.userSettingsRepository,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.DashboardEditor.route) {
            DashboardEditorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPicker = { panelId, slotIndex ->
                    navController.navigate(Screen.WidgetPicker.createRoute(panelId, slotIndex))
                }
            )
        }

        composable(
            route = Screen.WidgetPicker.route,
            arguments = listOf(
                navArgument("panelId") { type = NavType.StringType },
                navArgument("slotIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val panelId = backStackEntry.arguments?.getString("panelId") ?: ""
            val slotIndex = backStackEntry.arguments?.getInt("slotIndex") ?: 0

            WidgetPickerScreen(
                panelId = panelId,
                slotIndex = slotIndex,
                widgetRegistry = appContainer.widgetRegistry,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSelectWidget = { widgetTypeId ->
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.WidgetConfiguration.route,
            arguments = listOf(
                navArgument("instanceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val instanceId = backStackEntry.arguments?.getString("instanceId") ?: ""

            WidgetConfigurationScreen(
                instanceId = instanceId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
