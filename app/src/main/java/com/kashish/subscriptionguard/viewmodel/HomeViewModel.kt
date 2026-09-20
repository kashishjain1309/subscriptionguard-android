package com.kashish.subscriptionguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashish.subscriptionguard.data.model.Subscription
import com.kashish.subscriptionguard.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val subscriptions: List<Subscription>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repository: SubscriptionRepository = SubscriptionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _pendingReconciliations = MutableStateFlow<List<Subscription>>(emptyList())
    val pendingReconciliations: StateFlow<List<Subscription>> = _pendingReconciliations.asStateFlow()

    init {
        loadSubscriptions()
    }

    fun loadSubscriptions() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            repository.getSubscriptionsByUser("ACTIVE")
                .onSuccess { subscriptions ->
                    _uiState.value = HomeUiState.Success(subscriptions)
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(error.message ?: "Something went wrong")
                }

            repository.getSubscriptionsByUser("RECONCILIATION_REQUIRED")
                .onSuccess { _pendingReconciliations.value = it }
        }
    }

    fun resolveReconciliation(subscriptionId: String, action: String) {
        viewModelScope.launch {
            repository.reconcileSubscription(subscriptionId, action)
                .onSuccess {
                    _pendingReconciliations.value = _pendingReconciliations.value.filterNot { it.id == subscriptionId }
                    loadSubscriptions()
                }
        }
    }
}