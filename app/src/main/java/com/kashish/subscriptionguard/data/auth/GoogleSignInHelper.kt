package com.kashish.subscriptionguard.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kashish.subscriptionguard.BuildConfig

object GoogleSignInHelper {

    suspend fun signIn(context: Context): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)

            val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
            Result.success(credential.idToken)

        }
        catch (e: GetCredentialException) {
            Result.failure(e)
        }
    }
}