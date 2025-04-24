package com.wang.ghc.di

import com.wang.ghc.network.GitHubOAuthService
import com.wang.ghc.network.AccessTokenResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OAuthRetrofit

@Module
@InstallIn(SingletonComponent::class)
object OAuthModule {
    @OAuthRetrofit
    @Provides
    @Singleton
    fun provideOAuthRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val newRequest = original.newBuilder()
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://github.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOAuthService(@OAuthRetrofit retrofit: Retrofit): GitHubOAuthService {
        return retrofit.create(GitHubOAuthService::class.java)
    }
}
