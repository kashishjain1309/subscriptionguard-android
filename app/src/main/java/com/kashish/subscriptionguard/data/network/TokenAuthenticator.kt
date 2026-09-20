package com.kashish.subscriptionguard.data.network

import android.content.Context
import com.kashish.subscriptionguard.data.local.TokenManager
import com.kashish.subscriptionguard.data.model.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val context: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        if (response.priorResponse != null) {
            return null
        }

        val tokenManager = TokenManager(context)

        val newAccessToken = runBlocking {
            val refreshToken = tokenManager.getRefreshToken() ?: return@runBlocking null

            try {
                val refreshResponse = RetrofitClient.plainAuthApi.refreshToken(
                    RefreshTokenRequest(refreshToken)
                )
                val authResponse = refreshResponse.data ?: return@runBlocking null

                tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                authResponse.accessToken
            }
            catch (e: Exception) {
                null
            }
        }

        if (newAccessToken == null) {
            runBlocking { tokenManager.clearTokens() }
        }

        return if (newAccessToken != null) {
            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
        else {
            null
        }
    }
}