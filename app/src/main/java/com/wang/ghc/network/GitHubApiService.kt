package com.wang.ghc.network

import com.wang.ghc.model.GitHubContentResponse
import com.wang.ghc.model.Repo
import com.wang.ghc.model.IssueRequest
import com.wang.ghc.model.IssueResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {

    data class SearchResponse(val items: List<Repo>)


    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1
    ): Response<SearchResponse>


    @GET("/repos/{owner}/{repo}/contents/{path}")
    suspend fun getFileContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String
    ): Response<ResponseBody>

    @GET("/repos/{owner}/{repo}/contents/{path}")
    suspend fun getRepositoryContents(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String
    ): Response<List<GitHubContentResponse>>


    @Headers("Accept: application/vnd.github+json", "X-GitHub-Api-Version: 2022-11-28")
    @GET("/user/repos")
    suspend fun getUserRepositories(
        @Header("Authorization") token: String
    ): Response<List<Repo>>

    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body issueRequest: IssueRequest,
        @Header("Authorization") token: String
    ): Response<IssueResponse>
}



interface GitHubOAuthService {
    @FormUrlEncoded
    @POST("login/oauth/access_token")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): Response<AccessTokenResponse>
}
