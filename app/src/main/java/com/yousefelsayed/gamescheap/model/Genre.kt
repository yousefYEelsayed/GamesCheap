package com.yousefelsayed.gamescheap.model

import com.google.gson.annotations.SerializedName

data class Genre(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String
)