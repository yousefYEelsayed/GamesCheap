package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefelsayed.gamescheap.model.Games
import com.yousefelsayed.gamescheap.repository.GamesRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GamesViewModel(private val gamesRepository: GamesRepository): ViewModel() {

    private val errorHandler = CoroutineExceptionHandler{_, t ->
        run{
            requestStatus.postValue(t.message.toString())
        }
    }
    fun startGamesRequest(){
        viewModelScope.launch(Dispatchers.IO+errorHandler) {
            gamesRepository.getGames()
        }
    }
    val games: LiveData<Games> get() = gamesRepository.games
    val requestStatus get() = gamesRepository.requestStatus

}