package com.deepseek.view.data.api

import com.deepseek.view.data.model.BalanceResponse
import com.deepseek.view.data.model.UsageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Retrofit interface for DeepSeek Platform API.
 * Base URL: https://api.deepseek.com/
 */
interface DeepSeekApi {

    /**
     * Get user balance.
     * Requires Authorization: Bearer <api_key>
     */
    @GET("user/balance")
    suspend fun getBalance(
        @Header("Authorization") authHeader: String
    ): Response<BalanceResponse>

    /**
     * Get token usage for a date range.
     * This mirrors the platform's usage endpoint.
     */
    @GET("user/usage")
    suspend fun getUsage(
        @Header("Authorization") authHeader: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<UsageResponse>
}
