package com.yousefelsayed.gamescheap.model

import com.google.gson.annotations.SerializedName

data class ReleaseDate(
    @SerializedName("coming_soon")
    val coming_soon: Boolean,
    @SerializedName("date")
    val date: String
)