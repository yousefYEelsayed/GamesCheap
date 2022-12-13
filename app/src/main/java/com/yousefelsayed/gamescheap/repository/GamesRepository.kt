package com.yousefelsayed.gamescheap.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.api.ApiUtilities
import com.yousefelsayed.gamescheap.model.Games
import javax.inject.Inject

class GamesRepository @Inject constructor(private val apiInterface: ApiInterface) {

    private val gamesLiveData = MutableLiveData<Games>()
    val requestStatus = MutableLiveData("")

    val games: LiveData<Games> get() = gamesLiveData

    suspend fun getGames(){
        val result = apiInterface.getGames(ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_TOP_GAMES_URL)
        if (result.body() != null){
            gamesLiveData.postValue(result.body())
            requestStatus.postValue("SUCCESS")
        }
    }

}