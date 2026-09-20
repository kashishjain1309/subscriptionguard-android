package com.kashish.subscriptionguard.data.model

data class Subscription(
    val id: String,
    val name: String,
    val cost: String,
    val category: String,
    val type: String,
    val status: String,
    val intervalCount: Int,
    val billingInterval: String,
    val cycleEndDate: String
)

data class CreateSubscriptionRequest(
    val name: String,
    val cost: String,
    val category: String,
    val type: String,
    val intervalCount: Int,
    val billingInterval: String,
    val cycleStartDate: String
)

data class UpdateSubscriptionRequest(
    val name: String? = null,
    val cost: String? = null,
    val category: String? = null,
    val intervalCount: Int? = null,
    val billingInterval: String? = null,
    val cycleStartDate: String? = null
)