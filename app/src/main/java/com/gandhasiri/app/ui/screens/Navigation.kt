package com.gandhasiri.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gandhasiri.app.viewmodel.GandhaSiriViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddTree : Screen("add_tree")
    object TreeDetail : Screen("tree_detail/{treeId}") {
        fun createRoute(treeId: String) = "tree_detail/$treeId"
    }
    object GrowthTracker : Screen("growth_tracker/{treeId}") {
        fun createRoute(treeId: String) = "growth_tracker/$treeId"
    }
    object Security : Screen("security")
    object LegalGuide : Screen("legal_guide")
    object Contacts : Screen("contacts")
    object MaturityCalculator : Screen("maturity_calculator")
}

@Composable
fun GandhaSiriNavHost(
    navController: NavHostController,
    viewModel: GandhaSiriViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.AddTree.route) {
            AddTreeScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            Screen.TreeDetail.route,
            arguments = listOf(navArgument("treeId") { type = NavType.StringType })
        ) { backStack ->
            val treeId = backStack.arguments?.getString("treeId") ?: return@composable
            TreeDetailScreen(treeId = treeId, navController = navController, viewModel = viewModel)
        }
        composable(
            Screen.GrowthTracker.route,
            arguments = listOf(navArgument("treeId") { type = NavType.StringType })
        ) { backStack ->
            val treeId = backStack.arguments?.getString("treeId") ?: return@composable
            GrowthTrackerScreen(treeId = treeId, navController = navController, viewModel = viewModel)
        }
        composable(Screen.Security.route) {
            SecurityScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.LegalGuide.route) {
            LegalGuideScreen(navController = navController)
        }
        composable(Screen.Contacts.route) {
            ContactsScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.MaturityCalculator.route) {
            MaturityCalculatorScreen(navController = navController, viewModel = viewModel)
        }
    }
}
