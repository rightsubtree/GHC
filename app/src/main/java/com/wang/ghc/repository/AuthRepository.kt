package com.wang.ghc.repository

import android.util.Log
import com.wang.ghc.network.AccessTokenResponse
import com.wang.ghc.network.GitHubApiService
import com.wang.ghc.network.GitHubOAuthService
import com.wang.ghc.util.SessionManager
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class AuthRepository @Inject constructor(
    private val authService: GitHubOAuthService,
    private val sessionManager: SessionManager
) {
    suspend fun exchangeOAuthToken(code: String): AccessTokenResponse = withContext(Dispatchers.IO) {
        Log.w("AuthRepository", "exchangeOAuthToken with code=$code")
        val response = authService.getAccessToken(
            clientId = "Ov23lid7vXOMHRrTYQBC",
            clientSecret = "3d5da1c63e30e552c80286e0ceb705dcf2ab2ebe",
            code = code
        )
        if (!response.isSuccessful) {
            throw Exception("OAuth token exchange failed: ${response.errorBody()?.string()}")
        }
        response.body()!!.also {
            sessionManager.saveAccessToken(it.access_token)
            Log.d("AuthRepository", "Saved access token: ${it.access_token}")
        }
    }
}