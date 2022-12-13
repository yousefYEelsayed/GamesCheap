package com.yousefelsayed.gamescheap.model

import com.google.gson.annotations.SerializedName

data class PcRequirements(
    @SerializedName("minimum")
    val minimum: String
)