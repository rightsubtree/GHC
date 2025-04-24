package com.wang.ghc.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.wang.ghc.network.GitHubApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val TAG = "NetworkModule"

    // 这个token是一个Personal Access Token，不是通过OAuth每次动态交换的，
    // 这个固定token用于匿名状态的查询
    // TODO: github 不让上传带有token的东西，这里先留空，后续微信发出，有token的话，查询次数比较宽裕
    private val defaultToken = ""

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("github_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @ApiRetrofit
    fun provideRetrofit(prefs: SharedPreferences): Retrofit {
        val interceptor = HttpLoggingInterceptor().apply {
            // 减少日志输出
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = prefs.getString("access_token", null)
                val request = chain.request().newBuilder()
                    // 不要用addHeader，否则容易Authorization 两次，反而会出现401错误
                    .header("Authorization", "Bearer ${token ?: defaultToken}")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(interceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                Log.d(TAG, "请求头: ${request.headers}")
                chain.proceed(request)
            }
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.header("X-RateLimit-Remaining")?.let {
                    Log.d(TAG, "GitHub API剩余请求次数: $it")
                    prefs.edit().putInt("RATE_LIMIT_REMAINING", it.toInt()).apply()
                }
                response
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubApiService(@ApiRetrofit retrofit: Retrofit): GitHubApiService {
        return retrofit.create(GitHubApiService::class.java)
    }


}

