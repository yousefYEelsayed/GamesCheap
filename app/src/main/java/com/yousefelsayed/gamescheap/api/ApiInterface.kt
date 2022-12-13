package com.yousefelsayed.gamescheap.api

import com.yousefelsayed.gamescheap.model.Games
import com.yousefelsayed.gamescheap.model.SearchModel
import com.yousefelsayed.gamescheap.model.StoresModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiInterface {

    @GET
    suspend fun getGames(@Url url:String): Response<Games>
    @GET
    suspend fun getStores(@Url url:String): Response<StoresModel>
    @GET
    suspend fun getSearchResults(@Url url:String): Response<SearchModel>
    @GET
    suspend fun getSteamAndEpicGames(@Url url:String): Response<Games>
    @GET
    suspend fun getGameDetails(@Url url:String): Response<String>
    @GET
    suspend fun getGameDeals(@Url url:String): Response<Games>
}