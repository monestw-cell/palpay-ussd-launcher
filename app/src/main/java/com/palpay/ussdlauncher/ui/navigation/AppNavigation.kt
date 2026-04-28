package com.palpay.ussdlauncher.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.palpay.ussdlauncher.ui.screens.*
import com.palpay.ussdlauncher.viewmodel.MainViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Recipients : Screen("recipients")
    object History : Screen("history")
    object Settings : Screen("settings")
    object SendMoney : Screen("send_money/{serviceKey}?phone={phone}&name={name}") {
        fun createRoute(serviceKey: String, phone: String = "", name: String = "") =
            "send_money/$serviceKey?phone=${Uri.encode(phone)}&name=${Uri.encode(name)}"
    }
}

@Composable
fun AppNavigation(viewModel: MainViewModel, initialServiceKey: String? = null) {
    val navController = rememberNavController()

    LaunchedEffect(initialServiceKey) {
        if (!initialServiceKey.isNullOrBlank()) {
            navController.navigate(Screen.SendMoney.createRoute(initialServiceKey)) {
                popUpTo(Screen.Home.route) { inclusive = false }
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                onServiceClick = { serviceKey ->
                    navController.navigate(Screen.SendMoney.createRoute(serviceKey))
                },
                onRecipientsClick = { navController.navigate(Screen.Recipients.route) },
                onHistoryClick = { navController.navigate(Screen.History.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Recipients.route) {
            RecipientsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onRecipientSelected = { recipient ->
                    navController.navigate(
                        Screen.SendMoney.createRoute(
                            serviceKey = "bank_palestine",
                            phone = recipient.phone,
                            name = recipient.name
                        )
                    )
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SendMoney.route,
            arguments = listOf(
                navArgument("serviceKey") { type = NavType.StringType },
                navArgument("phone") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val serviceKey = backStackEntry.arguments?.getString("serviceKey") ?: "bank_palestine"
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            SendMoneyScreen(
                serviceKey = serviceKey,
                viewModel = viewModel,
                preselectedPhone = phone,
                preselectedName = name,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
