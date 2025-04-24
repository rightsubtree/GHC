package com.wang.ghc.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    data class GitHubConfig(
        val clientId: String,
        val clientSecret: String,
        val redirectUri: String
    )

    @Provides
    @Singleton
    fun provideGitHubConfig(): GitHubConfig {
        return GitHubConfig(
            clientId = "Ov23lid7vXOMHRrTYQBC",
            clientSecret = "3d5da1c63e30e552c80286e0ceb705dcf2ab2ebe",
            redirectUri = "ghclient://oauth-callback"
        )
    }


}