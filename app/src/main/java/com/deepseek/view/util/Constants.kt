package com.deepseek.view.util

object Constants {
    // SharedPreferences keys
    const val PREFS_NAME = "deepseek_prefs"
    const val KEY_USER_PHONE = "user_phone"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_LOGIN_METHOD = "login_method"

    // Encrypted prefs
    const val ENCRYPTED_PREFS_NAME = "deepseek_secure_prefs"
    const val KEY_API_KEY = "api_key_encrypted"
    const val KEY_API_KEY_MASKED = "api_key_masked"

    // Login method
    const val LOGIN_LOCAL = "local"

    // Navigation routes
    object Routes {
        const val LOGIN = "login"
        const val HOME = "home"
        const val API_KEY = "api_key"
        const val RECHARGE = "recharge"
        const val CHAT = "chat"
    }
}