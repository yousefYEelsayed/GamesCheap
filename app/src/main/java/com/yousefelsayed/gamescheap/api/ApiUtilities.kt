package com.yousefelsayed.gamescheap.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


public object ApiUtilities {
    //Cheap Shark Api
    public const val CHEAP_SHARK_BASE_URL = "https://www.cheapshark.com"
    public const val CHEAP_SHARK_STORES_DEALS_URL = "/api/1.0/deals?"
    public const val CHEAP_SHARK_TOP_GAMES_URL = "/api/1.0/deals?upperPrice=500&pageSize=15"
    public const val CHEAP_SHARK_STEAM_EPIC_GAMES = "/api/1.0/deals?storeID=1,25&upperPrice=500&pageSize=15"
    public const val CHEAP_SHARK_SEARCH = "/api/1.0/games?title="
    public const val CHEAP_SHARK_STORES_URL = "/api/1.0/stores"
    public const val CHEAP_SHARK_REDIRECT_LINK = "https://www.cheapshark.com/redirect?dealID="
    //Steam Api
    public const val STEAM_BASE_URL = "https://store.steampowered.com/api"
    public const val STEAM_GAME_DETAILS_URL = "/appdetails?appids="
    //Stores Image URL
    public const val STORE_1 = "/img/stores/banners/0.png"
    public const val STORE_2 = "/img/stores/banners/1.png"
    public const val STORE_3 = "/img/stores/banners/2.png"
    public const val STORE_4 = "/img/stores/banners/3.png"
    public const val STORE_5 = "/img/stores/banners/4.png"
    public const val STORE_6 = "/img/stores/banners/5.png"
    public const val STORE_7 = "/img/stores/banners/6.png"
    public const val STORE_8 = "/img/stores/banners/7.png"
    public const val STORE_9 = "/img/stores/banners/8.png"
    public const val STORE_10 = "/img/stores/banners/9.png"
    public const val STORE_11 = "/img/stores/banners/10.png"
    public const val STORE_12 = "/img/stores/banners/11.png"
    public const val STORE_13 = "/img/stores/banners/12.png"
    public const val STORE_14 = "/img/stores/banners/13.png"
    public const val STORE_15 = "/img/stores/banners/14.png"
    public const val STORE_16 = "/img/stores/banners/15.png"
    public const val STORE_17 = "/img/stores/banners/16.png"
    public const val STORE_18 = "/img/stores/banners/17.png"
    public const val STORE_19 = "/img/stores/banners/18.png"
    public const val STORE_20 = "/img/stores/banners/19.png"
    public const val STORE_21 = "/img/stores/banners/20.png"
    public const val STORE_22 = "/img/stores/banners/21.png"
    public const val STORE_23 = "/img/stores/banners/22.png"
    public const val STORE_24 = "/img/stores/banners/23.png"
    public const val STORE_25 = "/img/stores/banners/24.png"
    public const val STORE_26 = "/img/stores/banners/25.png"
    public const val STORE_27 = "/img/stores/banners/26.png"
    public const val STORE_28 = "/img/stores/banners/27.png"
    public const val STORE_29 = "/img/stores/banners/28.png"
    public const val STORE_30 = "/img/stores/banners/29.png"
    public const val STORE_31 = "/img/stores/banners/30.png"
    public const val STORE_32 = "/img/stores/banners/31.png"
    public const val STORE_33 = "/img/stores/banners/32.png"

    fun getInstance(): Retrofit{
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder().baseUrl(CHEAP_SHARK_BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient).build()
    }
}