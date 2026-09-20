package com.kashish.subscriptionguard.data.repository

import com.kashish.subscriptionguard.data.model.CreateSubscriptionRequest
import com.kashish.subscriptionguard.data.model.Subscription
import com.kashish.subscriptionguard.data.model.UpdateSubscriptionRequest
import com.kashish.subscriptionguard.data.network.RetrofitClient

class SubscriptionRepository {

    private val api = RetrofitClient.subscriptionApi

    suspend fun getSubscriptionsByUser(status: String = "ACTIVE"): Result<List<Subscription>> {
        return try {
            val response = api.getSubscriptionsByUser(status)
            val subscriptions = response.data ?: emptyList()
            Result.success(subscriptions)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSubscriptionById(id: String): Result<Subscription> {
        return try {
            val response = api.getSubscriptionById(id)
            val subscription = response.data ?: return Result.failure(Exception("No data in response"))
            Result.success(subscription)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSubscription(request: CreateSubscriptionRequest): Result<Unit> {
        return try {
            api.createSubscription(request)
            Result.success(Unit)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSubscription(id: String, request: UpdateSubscriptionRequest): Result<Unit> {
        return try {
            api.updateSubscription(id, request)
            Result.success(Unit)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reconcileSubscription(id: String, action: String): Result<Unit> {
        return try {
            api.reconcileSubscription(id, action)
            Result.success(Unit)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelSubscription(id: String): Result<Unit> {
        return try {
            api.cancelSubscription(id)
            Result.success(Unit)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }
}