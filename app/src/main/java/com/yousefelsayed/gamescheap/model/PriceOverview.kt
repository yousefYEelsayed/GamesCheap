package com.yousefelsayed.gamescheap.model

import com.google.gson.annotations.SerializedName

data class PriceOverview(
    @SerializedName("currency")
    val currency: String,
    @SerializedName("discount_percent")
    val discount_percent: Int,
    @SerializedName("final")
    val final: Int,
    @SerializedName("final_formatted")
    val final_formatted: String,
    @SerializedName("initial")
    val initial: Int,
    @SerializedName("initial_formatted")
    val initial_formatted: String
)