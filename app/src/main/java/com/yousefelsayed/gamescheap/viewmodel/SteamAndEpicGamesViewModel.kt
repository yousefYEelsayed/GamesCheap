package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefelsayed.gamescheap.model.Games
import com.yousefelsayed.gamescheap.repository.SteamAndEpicGamesRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class SteamAndEpicGamesViewModel(private val steamAndEpicGamesRepository: SteamAndEpicGamesRepository): ViewModel(){

    private val errorHandler = CoroutineExceptionHandler{_,t ->
        run{
            requestStatus.postValue(t.message.toString())
        }
    }
    fun startSteamEpicGamesRequest(){
        viewModelScope.launch(Dispatchers.IO+errorHandler) {
            steamAndEpicGamesRepository.getSteamAndEpicGames()
        }
    }

    val games: LiveData<Games> get() = steamAndEpicGamesRepository.games
    val requestStatus = steamAndEpicGamesRepository.requestStatus
}
