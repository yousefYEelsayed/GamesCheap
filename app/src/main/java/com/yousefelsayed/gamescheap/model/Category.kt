package com.yousefelsayed.gamescheap.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Int
)