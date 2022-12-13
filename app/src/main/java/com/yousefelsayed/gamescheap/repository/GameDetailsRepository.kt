package com.yousefelsayed.gamescheap.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.api.ApiUtilities

class GameDetailsRepository(private val apiInterface: ApiInterface,private val gameSteamID: String) {

    private val gameDetailsLiveData = MutableLiveData<String>()
    var requestStatus = MutableLiveData("")

    val gameDetails: LiveData<String> get() = gameDetailsLiveData

    suspend fun getGameDetails(){
        val result = apiInterface.getGameDetails(ApiUtilities.STEAM_BASE_URL+ApiUtilities.STEAM_GAME_DETAILS_URL+gameSteamID)
        if (result.body() != null){
            gameDetailsLiveData.postValue(result.body())
            requestStatus.postValue("SUCCESS")
        }
    }

}