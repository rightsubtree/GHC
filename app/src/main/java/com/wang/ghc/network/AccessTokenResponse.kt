package com.wang.ghc.network

data class AccessTokenResponse(
    val access_token: String,
    val scope: String,
    val token_type: String
)