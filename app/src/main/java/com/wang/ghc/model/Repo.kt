package com.wang.ghc.model

data class Repo(
    val id: Long,
    val name: String,
    val owner: Owner,
    val description: String?,
    val language: String?,
    val stargazers_count: Int,
    val license: License?,
    val visibility: String
) {
    data class Owner(
        val login: String,
        val avatar_url: String
    )

    data class License(val key: String, val name: String)
}