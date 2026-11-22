package com.kashish.subscriptionguard.data.network

import android.content.Context
import com.kashish.subscriptionguard.BuildConfig
import com.kashish.subscriptionguard.data.network.api.AuthApi
import com.kashish.subscriptionguard.data.network.api.SubscriptionApi
import com.kashish.subscriptionguard.data.network.api.UserApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private lateinit var retrofit: Retrofit
    private lateinit var plainRetrofit: Retrofit

    fun init(context: Context) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context.applicationContext))
            .addInterceptor(loggingInterceptor)
            .authenticator(TokenAuthenticator(context.applicationContext))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val plainOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        plainRetrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(plainOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val plainAuthApi: AuthApi by lazy { plainRetrofit.create(AuthApi::class.java) }
    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
    val subscriptionApi: SubscriptionApi by lazy { retrofit.create(SubscriptionApi::class.java) }
}