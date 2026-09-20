package com.kashish.subscriptionguard.data.network.api

import com.kashish.subscriptionguard.data.model.CreateSubscriptionRequest
import com.kashish.subscriptionguard.data.model.Subscription
import com.kashish.subscriptionguard.data.model.UpdateSubscriptionRequest
import com.kashish.subscriptionguard.data.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SubscriptionApi {

    @GET("api/subscriptions")
    suspend fun getSubscriptionsByUser(
        @Query("status") status: String = "ACTIVE"
    ): ApiResponse<List<Subscription>>

    @GET("api/subscriptions/{id}")
    suspend fun getSubscriptionById(@Path("id") id: String): ApiResponse<Subscription>

    @POST("api/subscriptions")
    suspend fun createSubscription(@Body request: CreateSubscriptionRequest): ApiResponse<Map<String, String>>

    @PATCH("api/subscriptions/{id}")
    suspend fun updateSubscription(
        @Path("id") id: String,
        @Body request: UpdateSubscriptionRequest
    ): ApiResponse<Unit>

    @PATCH("api/subscriptions/{id}/reconcile")
    suspend fun reconcileSubscription(
        @Path("id") id: String,
        @Query("action") action: String
    ): ApiResponse<Unit>

    @DELETE("api/subscriptions/{id}")
    suspend fun cancelSubscription(@Path("id") id: String): ApiResponse<Unit>

}