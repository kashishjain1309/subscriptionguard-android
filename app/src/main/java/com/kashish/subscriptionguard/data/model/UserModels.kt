package com.kashish.subscriptionguard.data.model

data class User(
    val id: String,
    val name: String,
    val email: String
)

data class FcmTokenRequest(
    val fcmToken: String
)