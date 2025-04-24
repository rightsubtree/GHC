package com.wang.ghc.model

import com.google.gson.annotations.SerializedName

data class GitHubContentResponse(
    @SerializedName("name") val name: String,
    @SerializedName("path") val path: String,
    @SerializedName("type") val type: String,
    @SerializedName("download_url") val downloadUrl: String?,
    @SerializedName("size") val size: Int,
    @SerializedName("sha") val sha: String
) {
    fun toContentItem() = GitHubContentItem(
        name = name,
        path = path,
        type = type,
        downloadUrl = downloadUrl
    )
}