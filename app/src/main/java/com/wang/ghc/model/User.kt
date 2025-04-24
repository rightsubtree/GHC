package com.wang.ghc.model

data class User(
    val login: String,
    val id: Long,
    val accessToken: String,
    val avatarUrl: String? = null,
    val name: String? = null
)