package com.kashish.subscriptionguard.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashish.subscriptionguard.data.notification.FcmTokenProvider
import com.kashish.subscriptionguard.data.repository.AuthRepository
import com.kashish.subscriptionguard.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class AuthViewModel(context: Context) : ViewModel() {

    private val authRepository = AuthRepository(context)
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            authRepository.loginWithGoogle(idToken)
                .onSuccess {
                    _uiState.value = LoginUiState.Success
                    registerDeviceToken()
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Error(error.message ?: "Login failed")
                }
        }
    }

    private fun registerDeviceToken() {
        viewModelScope.launch {
            try {
                val token = FcmTokenProvider.getToken()
                userRepository.registerFcmToken(token)
            }
            catch (e: Exception) {
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }

    fun setError(message: String) {
        _uiState.value = LoginUiState.Error(message)
    }
}