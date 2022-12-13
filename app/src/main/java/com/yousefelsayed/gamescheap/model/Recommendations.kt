package com.yousefelsayed.gamescheap.model

import com.google.gson.annotations.SerializedName

data class Recommendations(
    @SerializedName("total")
    val total: Int?
)