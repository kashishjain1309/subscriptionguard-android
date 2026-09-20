package com.kashish.subscriptionguard.data.network

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T?,
    val timestamp: String
)