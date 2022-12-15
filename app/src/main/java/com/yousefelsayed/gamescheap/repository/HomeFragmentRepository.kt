package com.yousefelsayed.gamescheap.repository

import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.api.ApiUtilities
import com.yousefelsayed.gamescheap.model.Games
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class HomeFragmentRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun getSteamAndEpicGames(): Flow<Games>{
        return flow {
            emit(apiInterface.getSteamAndEpicGames(ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_STEAM_EPIC_GAMES))
        }.map {
            it.body()!!
        }

        /*val result = apiInterface.getSteamAndEpicGames(ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_STEAM_EPIC_GAMES)
        if (result.code() == 200 && result.body() != null){
            gamesLiveData.value = result.body()!!
            //requestStatus.postValue("SUCCESS")
        }*/
    }
    suspend fun getTopGames(): Flow<Games>{
        return flow {
            emit(apiInterface.getGames(ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_TOP_GAMES_URL))
        }.map {
            it.body()!!
        }
    }
}