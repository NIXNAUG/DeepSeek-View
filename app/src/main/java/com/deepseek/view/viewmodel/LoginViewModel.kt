package com.deepseek.view.viewmodel

import android.app.Application
import android.webkit.CookieManager
import androidx.lifecycle.AndroidViewModel
import com.deepseek.view.data.local.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val isLoading: Boolean = true,      // WebView loading
    val currentUrl: String = "",
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = PreferencesManager(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    // DeepSeek login URL patterns
    companion object {
        // Load the platform home — redirects to login if not authenticated
        const val DEEPSEEK_PLATFORM = "https://platform.deepseek.com/"
        // URLs that indicate the user is on a login/auth page
        private val LOGIN_URL_PATTERNS = listOf("login", "signin", "sign_in", "auth")
    }

    init {
        if (prefsManager.isLoggedIn) {
            _uiState.update { it.copy(isLoggedIn = true) }
        }
    }

    /** Called by WebView when URL changes. Returns true if login is detected as complete. */
    fun onUrlChanged(url: String): Boolean {
        _uiState.update { it.copy(currentUrl = url, isLoading = false) }

        // Check if we've navigated away from login pages → login succeeded
        val isOnLoginPage = LOGIN_URL_PATTERNS.any { pattern ->
            url.lowercase().contains(pattern)
        }

        if (!isOnLoginPage && url.startsWith("https://platform.deepseek.com")) {
            // User is now on a platform page (not login) → authenticated
            onLoginSuccess()
            return true
        }
        return false
    }

    fun onPageStarted() {
        _uiState.update { it.copy(isLoading = true) }
    }

    fun onPageFinished(url: String) {
        _uiState.update { it.copy(isLoading = false, currentUrl = url) }
    }

    fun onError(message: String) {
        _uiState.update { it.copy(isLoading = false, error = message) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun logout() {
        // Clear WebView cookies
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        prefsManager.clearAll()
        _uiState.update { LoginUiState() }
    }

    private fun onLoginSuccess() {
        // Persist session cookies automatically via CookieManager
        prefsManager.isLoggedIn = true
        prefsManager.loginMethod = "deepseek"
        _uiState.update { it.copy(isLoggedIn = true, isLoading = false, error = null) }
    }
}