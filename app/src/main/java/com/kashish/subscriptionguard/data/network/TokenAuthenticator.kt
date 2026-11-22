package com.kashish.subscriptionguard.data.network

import android.content.Context
import com.kashish.subscriptionguard.data.local.TokenManager
import com.kashish.subscriptionguard.data.model.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException

class TokenAuthenticator(private val context: Context) : Authenticator {

    private val tokenManager = TokenManager(context.applicationContext)

    override fun authenticate(route: Route?, response: Response): Request? {

        if (response.priorResponse != null) {
            return null
        }

        synchronized(this) {
            val failedToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            val currentAccessToken = runBlocking { tokenManager.getAccessToken() }

            if (currentAccessToken != null && currentAccessToken != failedToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

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
                catch (e: HttpException) {
                    if (e.code() == 401 || e.code() == 403) {
                        tokenManager.clearTokens()
                    }
                    null
                }
                catch (e: Exception) {
                    null
                }
            }

            return if (newAccessToken != null) {
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            } else {
                null
            }
        }
    }
}