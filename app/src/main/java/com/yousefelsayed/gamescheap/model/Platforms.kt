package com.yousefelsayed.gamescheap.model

import com.google.gson.annotations.SerializedName

data class Platforms(
    @SerializedName("linux")
    val linux: Boolean,
    @SerializedName("mac")
    val mac: Boolean,
    @SerializedName("windows")
    val windows: Boolean
)