package com.deepseek.view.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.deepseek.view.ui.screens.ApiKeyScreen
import com.deepseek.view.ui.screens.ChatScreen
import com.deepseek.view.ui.screens.HomeScreen
import com.deepseek.view.ui.screens.LoginScreen
import com.deepseek.view.ui.screens.RechargeScreen
import com.deepseek.view.util.Constants.Routes
import com.deepseek.view.viewmodel.ApiKeyViewModel
import com.deepseek.view.viewmodel.LoginViewModel
import com.deepseek.view.viewmodel.MainViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    startDestination: String = Routes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Home / Dashboard
        composable(Routes.HOME) {
            val mainViewModel: MainViewModel = viewModel()
            HomeScreen(
                viewModel = mainViewModel,
                onNavigateToApiKey = {
                    navController.navigate(Routes.API_KEY)
                },
                onNavigateToRecharge = {
                    navController.navigate(Routes.RECHARGE)
                },
                onNavigateToChat = {
                    navController.navigate(Routes.CHAT)
                },
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // API Key management
        composable(Routes.API_KEY) {
            val apiKeyViewModel: ApiKeyViewModel = viewModel()
            ApiKeyScreen(
                viewModel = apiKeyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Recharge (WebView)
        composable(Routes.RECHARGE) {
            RechargeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Chat (WebView)
        composable(Routes.CHAT) {
            ChatScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}