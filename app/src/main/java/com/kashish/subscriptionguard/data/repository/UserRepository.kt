package com.kashish.subscriptionguard.data.repository

import com.kashish.subscriptionguard.data.model.FcmTokenRequest
import com.kashish.subscriptionguard.data.model.User
import com.kashish.subscriptionguard.data.network.RetrofitClient

class UserRepository {

    private val api = RetrofitClient.userApi

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = api.getCurrentUser()
            val user = response.data ?: return Result.failure(Exception("No data in response"))
            Result.success(user)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerFcmToken(token: String): Result<Unit> {
        return try {
            api.registerFcmToken(FcmTokenRequest(token))
            Result.success(Unit)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }
}