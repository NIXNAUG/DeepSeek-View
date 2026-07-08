package com.deepseek.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.deepseek.view.data.local.PreferencesManager
import com.deepseek.view.navigation.AppNavGraph
import com.deepseek.view.ui.theme.DeepSeekViewTheme
import com.deepseek.view.util.Constants
import com.deepseek.view.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefsManager = PreferencesManager(this)
        val isLoggedIn = prefsManager.isLoggedIn
        val startDest = if (isLoggedIn) Constants.Routes.HOME else Constants.Routes.LOGIN

        setContent {
            DeepSeekViewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val loginViewModel: LoginViewModel = viewModel()

                    AppNavGraph(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        startDestination = startDest
                    )
                }
            }
        }
    }
}