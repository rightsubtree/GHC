package com.wang.ghc.model

data class GitHubContentItem(
    val name: String,
    val path: String,
    val type: String,
    val downloadUrl: String?
)