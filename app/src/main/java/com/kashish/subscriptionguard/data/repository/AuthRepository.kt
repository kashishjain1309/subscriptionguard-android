package com.kashish.subscriptionguard.data.repository

import android.content.Context
import com.kashish.subscriptionguard.data.local.TokenManager
import com.kashish.subscriptionguard.data.model.AuthResponse
import com.kashish.subscriptionguard.data.model.GoogleAuthRequest
import com.kashish.subscriptionguard.data.model.RefreshTokenRequest
import com.kashish.subscriptionguard.data.network.RetrofitClient

class AuthRepository(context: Context) {

    private val api = RetrofitClient.authApi
    private val tokenManager = TokenManager(context.applicationContext)

    suspend fun loginWithGoogle(idToken: String): Result<AuthResponse> {
        return try {
            val response = api.loginWithGoogle(GoogleAuthRequest(idToken))
            val authResponse = response.data
                ?: return Result.failure(Exception("No data in response"))

            tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
            Result.success(authResponse)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshAccessToken(): Result<AuthResponse> {
        return try {
            val refreshToken = tokenManager.getRefreshToken()
                ?: return Result.failure(Exception("No refresh token available"))

            val response = api.refreshToken(RefreshTokenRequest(refreshToken))
            val authResponse = response.data
                ?: return Result.failure(Exception("No data in response"))

            tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
            Result.success(authResponse)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        try {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken != null) {
                api.logout(RefreshTokenRequest(refreshToken))
            }
        }
        catch (e: Exception) {
        }
        finally {
            tokenManager.clearTokens()
        }
    }
}