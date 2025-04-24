package com.wang.ghc.repository

import android.util.Log
import com.wang.ghc.model.Repo
import com.wang.ghc.network.GitHubApiService
import javax.inject.Inject

class RepoRepository @Inject constructor(private val apiService: GitHubApiService) {
    suspend fun getPopularRepos(): List<Repo> {
        return (1..1).flatMap { page ->
            runCatching {
                apiService.searchRepositories(
                    query = "stars:>1000",
                    page = page
                ).body()?.items ?: emptyList()
            }.getOrDefault(emptyList())
        }
    }

    suspend fun getUserRepositories(token: String): List<Repo> {
        Log.d("RepoRepository", "Using Authorization: Bearer $token")
        val response = apiService.getUserRepositories("Bearer $token")
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            Log.e("RepoRepository", "Failed to get repos: ${response.code()} - ${response.message()}")
            emptyList()
        }
    }
}