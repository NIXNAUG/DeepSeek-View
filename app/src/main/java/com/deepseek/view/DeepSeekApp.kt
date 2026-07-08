package com.deepseek.view

import android.app.Application

class DeepSeekApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // No external service init needed.
        // Login uses local PBKDF2-hashed account stored in EncryptedSharedPreferences.
    }
}