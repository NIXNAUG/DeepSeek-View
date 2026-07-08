package com.deepseek.view.data.repository

import com.deepseek.view.data.api.RetrofitClient
import com.deepseek.view.data.local.SecureStorage
import com.deepseek.view.data.model.BalanceInfo
import com.deepseek.view.data.model.UsageResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Single source of truth for DeepSeek API data.
 */
class DeepSeekRepository(private val secureStorage: SecureStorage) {

    private val api = RetrofitClient.api

    /**
     * Fetch current balance. Returns null on failure.
     */
    suspend fun fetchBalance(): Result<BalanceInfo?> {
        return try {
            val authHeader = secureStorage.getAuthHeader()
                ?: return Result.failure(IllegalStateException("未设置 API Key"))

            val response = api.getBalance(authHeader)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.isAvailable == true && body.balanceInfos.isNotEmpty()) {
                    Result.success(body.balanceInfos.first())
                } else {
                    Result.success(null)
                }
            } else {
                Result.failure(Exception("获取余额失败: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch token usage for the last 30 days.
     */
    suspend fun fetchUsage(): Result<UsageResponse?> {
        return try {
            val authHeader = secureStorage.getAuthHeader()
                ?: return Result.failure(IllegalStateException("未设置 API Key"))

            val endDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val startDate = LocalDate.now().minusDays(30)
                .format(DateTimeFormatter.ISO_LOCAL_DATE)

            val response = api.getUsage(authHeader, startDate, endDate)
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("获取用量失败: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
