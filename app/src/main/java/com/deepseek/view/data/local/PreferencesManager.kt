package com.deepseek.view.data.local

import android.content.Context
import android.content.SharedPreferences
import com.deepseek.view.util.Constants

/**
 * Standard SharedPreferences for non-sensitive settings (login state, theme, etc.).
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(Constants.KEY_IS_LOGGED_IN, value).apply()

    var loginMethod: String
        get() = prefs.getString(Constants.KEY_LOGIN_METHOD, "") ?: ""
        set(value) = prefs.edit().putString(Constants.KEY_LOGIN_METHOD, value).apply()

    var userPhone: String
        get() = prefs.getString(Constants.KEY_USER_PHONE, "") ?: ""
        set(value) = prefs.edit().putString(Constants.KEY_USER_PHONE, value).apply()

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
