package com.deepseek.view.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.deepseek.view.util.Constants

/**
 * Encrypted storage for sensitive data (API keys).
 * Uses AndroidX Security Crypto library with AES-256 encryption.
 */
class SecureStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        Constants.ENCRYPTED_PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // ---- Generic key-value access (used by AccountManager etc.) ----

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return prefs.getString(key, null)
    }

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    // ---- API Key specific methods ----

    /**
     * Store DeepSeek API key encrypted.
     */
    fun saveApiKey(apiKey: String) {
        prefs.edit()
            .putString(Constants.KEY_API_KEY, apiKey)
            .putString(Constants.KEY_API_KEY_MASKED, maskKey(apiKey))
            .apply()
    }

    /**
     * Retrieve the decrypted API key.
     */
    fun getApiKey(): String? {
        return prefs.getString(Constants.KEY_API_KEY, null)
    }

    /**
     * Get masked version for display, e.g. "sk-****-abcd".
     */
    fun getMaskedApiKey(): String {
        return prefs.getString(Constants.KEY_API_KEY_MASKED, "") ?: ""
    }

    /**
     * Check if an API key has been saved.
     */
    fun hasApiKey(): Boolean {
        return !getApiKey().isNullOrBlank()
    }

    /**
     * Delete stored API key.
     */
    fun clearApiKey() {
        prefs.edit()
            .remove(Constants.KEY_API_KEY)
            .remove(Constants.KEY_API_KEY_MASKED)
            .apply()
    }

    /**
     * Build Authorization header value.
     */
    fun getAuthHeader(): String? {
        val key = getApiKey() ?: return null
        return "Bearer $key"
    }

    private fun maskKey(key: String): String {
        if (key.length <= 8) return "***"
        val prefix = key.take(5)
        val suffix = key.takeLast(4)
        return "$prefix****$suffix"
    }
}
