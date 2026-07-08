package com.deepseek.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deepseek.view.data.local.PreferencesManager
import com.deepseek.view.data.local.SecureStorage
import com.deepseek.view.data.model.BalanceInfo
import com.deepseek.view.data.model.DashboardData
import com.deepseek.view.data.model.UsageResponse
import com.deepseek.view.data.repository.DeepSeekRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val secureStorage = SecureStorage(application)
    private val prefsManager = PreferencesManager(application)
    private val repository = DeepSeekRepository(secureStorage)

    private val _dashboardData = MutableStateFlow(DashboardData())
    val dashboardData: StateFlow<DashboardData> = _dashboardData

    init {
        // Load masked key on start
        _dashboardData.update {
            it.copy(maskedApiKey = secureStorage.getMaskedApiKey())
        }
        // Auto-fetch if key is set
        if (secureStorage.hasApiKey()) {
            refreshAll()
        }
    }

    fun refreshAll() {
        viewModelScope.launch {
            _dashboardData.update { it.copy(isLoadingBalance = true, isLoadingUsage = true, error = null) }

            // Fetch balance and usage in parallel
            val balanceJob = launch { fetchBalanceInternal() }
            val usageJob = launch { fetchUsageInternal() }

            balanceJob.join()
            usageJob.join()
        }
    }

    fun refreshBalance() {
        viewModelScope.launch {
            _dashboardData.update { it.copy(isLoadingBalance = true, error = null) }
            fetchBalanceInternal()
        }
    }

    fun refreshUsage() {
        viewModelScope.launch {
            _dashboardData.update { it.copy(isLoadingUsage = true, error = null) }
            fetchUsageInternal()
        }
    }

    fun onApiKeyUpdated() {
        _dashboardData.update {
            it.copy(maskedApiKey = secureStorage.getMaskedApiKey())
        }
        refreshAll()
    }

    private suspend fun fetchBalanceInternal() {
        repository.fetchBalance()
            .onSuccess { balance ->
                _dashboardData.update {
                    it.copy(balance = balance, isLoadingBalance = false)
                }
            }
            .onFailure { e ->
                _dashboardData.update {
                    it.copy(isLoadingBalance = false, error = e.message)
                }
            }
    }

    private suspend fun fetchUsageInternal() {
        repository.fetchUsage()
            .onSuccess { usage ->
                _dashboardData.update {
                    it.copy(usage = usage, isLoadingUsage = false)
                }
            }
            .onFailure { e ->
                _dashboardData.update {
                    it.copy(isLoadingUsage = false, error = e.message)
                }
            }
    }
}
