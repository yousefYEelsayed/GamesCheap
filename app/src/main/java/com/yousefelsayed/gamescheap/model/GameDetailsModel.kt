package com.yousefelsayed.gamescheap.model

import com.google.gson.annotations.SerializedName

data class GameDetailsModel(
    @SerializedName("about_the_game")
    val about_the_game: String,
    @SerializedName("background")
    val background: String,
    @SerializedName("background_raw")
    val background_raw: String,
    @SerializedName("categories")
    val categories: List<Category>,
    @SerializedName("controller_support")
    val controller_support: String,
    @SerializedName("detailed_description")
    val detailed_description: String,
    @SerializedName("developers")
    val developers: List<String>,
    @SerializedName("genres")
    val genres: List<Genre>,
    @SerializedName("header_image")
    val header_image: String,
    @SerializedName("is_free")
    val is_free: Boolean,
    @SerializedName("legal_notice")
    val legal_notice: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("pc_requirements")
    val pc_requirements: PcRequirements,
    @SerializedName("platforms")
    val platforms: Platforms,
    @SerializedName("price_overview")
    val price_overview: PriceOverview,
    @SerializedName("publishers")
    val publishers: List<String>,
    @SerializedName("recommendations")
    val recommendations: Recommendations?,
    @SerializedName("release_date")
    val release_date: ReleaseDate,
    @SerializedName("required_age")
    val required_age: Int,
    @SerializedName("reviews")
    val reviews: String,
    @SerializedName("screenshots")
    val screenshots: List<Screenshot>,
    @SerializedName("short_description")
    val short_description: String,
    @SerializedName("steam_appid")
    val steam_appid: Int,
    @SerializedName("supported_languages")
    val supported_languages: String,
    @SerializedName("type")
    val type: String,
)