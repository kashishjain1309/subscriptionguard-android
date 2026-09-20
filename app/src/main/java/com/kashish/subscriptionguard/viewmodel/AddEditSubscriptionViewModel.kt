package com.kashish.subscriptionguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashish.subscriptionguard.data.model.CreateSubscriptionRequest
import com.kashish.subscriptionguard.data.model.Subscription
import com.kashish.subscriptionguard.data.model.UpdateSubscriptionRequest
import com.kashish.subscriptionguard.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SaveState {
    data object Idle : SaveState
    data object Saving : SaveState
    data object Success : SaveState
    data class Error(val message: String) : SaveState
}

class AddEditSubscriptionViewModel(
    private val subscriptionId: String? = null,
    private val repository: SubscriptionRepository = SubscriptionRepository()
) : ViewModel() {

    private val _existingSubscription = MutableStateFlow<Subscription?>(null)
    val existingSubscription: StateFlow<Subscription?> = _existingSubscription.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    val isEditMode: Boolean = subscriptionId != null

    init {
        if (subscriptionId != null) {
            loadExistingSubscription(subscriptionId)
        }
    }

    private fun loadExistingSubscription(id: String) {
        viewModelScope.launch {
            repository.getSubscriptionById(id)
                .onSuccess { subscription ->
                    _existingSubscription.value = subscription
                }
        }
    }

    fun createSubscription(request: CreateSubscriptionRequest) {
        viewModelScope.launch {
            _saveState.value = SaveState.Saving
            repository.createSubscription(request)
                .onSuccess { _saveState.value = SaveState.Success }
                .onFailure { error -> _saveState.value = SaveState.Error(error.message ?: "Failed to create subscription") }
        }
    }

    fun updateSubscription(request: UpdateSubscriptionRequest) {
        val id = subscriptionId ?: return
        viewModelScope.launch {
            _saveState.value = SaveState.Saving
            repository.updateSubscription(id, request)
                .onSuccess { _saveState.value = SaveState.Success }
                .onFailure { error -> _saveState.value = SaveState.Error(error.message ?: "Failed to update subscription") }
        }
    }
}