package com.deepseek.view.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deepseek.view.data.local.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApiKeyUiState(
    val apiKeyInput: String = "",
    val maskedApiKey: String = "",
    val hasExistingKey: Boolean = false,
    val isSaving: Boolean = false,
    val showKey: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class ApiKeyViewModel(application: Application) : AndroidViewModel(application) {

    private val secureStorage = SecureStorage(application)

    private val _uiState = MutableStateFlow(ApiKeyUiState())
    val uiState: StateFlow<ApiKeyUiState> = _uiState

    init {
        // Load existing key status
        val masked = secureStorage.getMaskedApiKey()
        _uiState.update {
            it.copy(
                hasExistingKey = masked.isNotEmpty(),
                maskedApiKey = masked
            )
        }
    }

    fun onApiKeyInputChange(key: String) {
        _uiState.update { it.copy(apiKeyInput = key, error = null, saveSuccess = false) }
    }

    fun toggleShowKey() {
        _uiState.update { it.copy(showKey = !it.showKey) }
    }

    fun saveApiKey() {
        val key = _uiState.value.apiKeyInput.trim()
        if (key.isEmpty()) {
            _uiState.update { it.copy(error = "请输入 API Key") }
            return
        }
        if (!key.startsWith("sk-")) {
            _uiState.update { it.copy(error = "API Key 格式不正确，应以 sk- 开头") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                secureStorage.saveApiKey(key)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true,
                        hasExistingKey = true,
                        maskedApiKey = secureStorage.getMaskedApiKey(),
                        apiKeyInput = "" // Clear input
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, error = "保存失败: ${e.localizedMessage}")
                }
            }
        }
    }

    fun deleteApiKey() {
        secureStorage.clearApiKey()
        _uiState.update {
            it.copy(
                hasExistingKey = false,
                maskedApiKey = "",
                apiKeyInput = "",
                saveSuccess = false,
                error = null
            )
        }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
