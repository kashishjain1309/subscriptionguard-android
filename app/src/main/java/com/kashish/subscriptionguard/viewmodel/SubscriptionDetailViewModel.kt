package com.kashish.subscriptionguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashish.subscriptionguard.data.model.Subscription
import com.kashish.subscriptionguard.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SubscriptionDetailUiState {
    data object Loading : SubscriptionDetailUiState
    data class Success(val subscription: Subscription) : SubscriptionDetailUiState
    data class Error(val message: String) : SubscriptionDetailUiState
}

class SubscriptionDetailViewModel(
    private val subscriptionId: String,
    private val repository: SubscriptionRepository = SubscriptionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<SubscriptionDetailUiState>(SubscriptionDetailUiState.Loading)
    val uiState: StateFlow<SubscriptionDetailUiState> = _uiState.asStateFlow()

    private val _cancelSuccess = MutableStateFlow(false)
    val cancelSuccess: StateFlow<Boolean> = _cancelSuccess.asStateFlow()

    init {
        loadSubscription()
    }

    private fun loadSubscription() {
        viewModelScope.launch {
            repository.getSubscriptionById(subscriptionId)
                .onSuccess { _uiState.value = SubscriptionDetailUiState.Success(it) }
                .onFailure { _uiState.value = SubscriptionDetailUiState.Error(it.message ?: "Failed to load subscription") }
        }
    }

    fun cancelSubscription() {
        viewModelScope.launch {
            repository.cancelSubscription(subscriptionId)
                .onSuccess { _cancelSuccess.value = true }
                .onFailure { _uiState.value = SubscriptionDetailUiState.Error(it.message ?: "Failed to cancel subscription") }
        }
    }
}