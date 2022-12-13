package com.yousefelsayed.gamescheap.model

import com.google.gson.annotations.SerializedName

data class Screenshot(
    @SerializedName("id")
    val id: Int,
    @SerializedName("path_full")
    val path_full: String,
    @SerializedName("path_thumbnail")
    val path_thumbnail: String
)