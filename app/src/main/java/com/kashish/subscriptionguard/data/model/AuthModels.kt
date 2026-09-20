package com.kashish.subscriptionguard.data.model

data class GoogleAuthRequest(
    val idToken: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)