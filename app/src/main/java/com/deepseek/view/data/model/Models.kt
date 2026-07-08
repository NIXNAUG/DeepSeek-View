package com.deepseek.view.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response from GET /user/balance
 */
data class BalanceResponse(
    @SerializedName("is_available")
    val isAvailable: Boolean,

    @SerializedName("balance_infos")
    val balanceInfos: List<BalanceInfo> = emptyList()
)

data class BalanceInfo(
    @SerializedName("currency")
    val currency: String,

    @SerializedName("total_balance")
    val totalBalance: String,

    @SerializedName("granted_balance")
    val grantedBalance: String,

    @SerializedName("topped_up_balance")
    val toppedUpBalance: String
)

/**
 * Response from GET /user/usage (hypothetical based on platform docs)
 */
data class UsageResponse(
    @SerializedName("total_tokens")
    val totalTokens: Long = 0,

    @SerializedName("prompt_tokens")
    val promptTokens: Long = 0,

    @SerializedName("completion_tokens")
    val completionTokens: Long = 0,

    @SerializedName("models")
    val models: List<ModelUsage> = emptyList(),

    @SerializedName("start_date")
    val startDate: String = "",

    @SerializedName("end_date")
    val endDate: String = ""
)

data class ModelUsage(
    @SerializedName("model_name")
    val modelName: String,

    @SerializedName("total_tokens")
    val totalTokens: Long
)

/**
 * Unified dashboard state
 */
data class DashboardData(
    val balance: BalanceInfo? = null,
    val usage: UsageResponse? = null,
    val maskedApiKey: String = "",
    val isLoadingBalance: Boolean = false,
    val isLoadingUsage: Boolean = false,
    val error: String? = null
)
