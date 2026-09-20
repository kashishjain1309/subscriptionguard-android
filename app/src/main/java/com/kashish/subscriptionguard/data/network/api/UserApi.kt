package com.kashish.subscriptionguard.data.network.api

import com.kashish.subscriptionguard.data.model.FcmTokenRequest
import com.kashish.subscriptionguard.data.model.User
import com.kashish.subscriptionguard.data.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {

    @GET("api/users/me")
    suspend fun getCurrentUser(): ApiResponse<User>

    @POST("api/users/fcmToken")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest): ApiResponse<Unit>

}