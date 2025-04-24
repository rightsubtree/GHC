package com.wang.ghc.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class IssueResponse(
    val id: Int,
    @SerializedName("node_id")
    val nodeId: String,
    val title: String,
    val body: String?,
    val state: String,
    val user: User,
    @SerializedName("created_at")
    val createdAt: Date
)