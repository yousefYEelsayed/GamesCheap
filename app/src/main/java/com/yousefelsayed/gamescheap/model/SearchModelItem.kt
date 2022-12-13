package com.yousefelsayed.gamescheap.model

data class SearchModelItem(
    val cheapest: String,
    val cheapestDealID: String,
    val external: String,
    val gameID: String,
    val internalName: String,
    val steamAppID: String,
    val thumb: String
)