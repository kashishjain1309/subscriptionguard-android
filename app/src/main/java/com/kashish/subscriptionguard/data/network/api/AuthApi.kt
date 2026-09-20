package com.kashish.subscriptionguard.data.network.api

import com.kashish.subscriptionguard.data.model.AuthResponse
import com.kashish.subscriptionguard.data.model.GoogleAuthRequest
import com.kashish.subscriptionguard.data.model.RefreshTokenRequest
import com.kashish.subscriptionguard.data.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/login/google")
    suspend fun loginWithGoogle(@Body request: GoogleAuthRequest): ApiResponse<AuthResponse>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(@Body request: RefreshTokenRequest): ApiResponse<Unit>

}