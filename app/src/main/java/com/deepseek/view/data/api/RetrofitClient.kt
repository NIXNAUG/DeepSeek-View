package com.deepseek.view.data.api

import com.deepseek.view.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit client for DeepSeek API calls.
 * Uses WebViewCookieJar to share login session cookies from the WebView,
 * so platform.deepseek.com login automatically authenticates API calls.
 */
object RetrofitClient {

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .cookieJar(WebViewCookieJar())   // shares cookies with WebView
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val api: DeepSeekApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.DEEPSEEK_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeepSeekApi::class.java)
    }
}