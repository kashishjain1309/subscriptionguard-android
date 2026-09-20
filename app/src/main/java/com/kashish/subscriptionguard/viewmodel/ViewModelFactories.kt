package com.kashish.subscriptionguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddEditSubscriptionViewModelFactory(
    private val subscriptionId: String?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AddEditSubscriptionViewModel(subscriptionId) as T
    }
}

class SubscriptionDetailViewModelFactory(private val subscriptionId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SubscriptionDetailViewModel(subscriptionId) as T
    }
}