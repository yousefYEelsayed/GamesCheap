package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefelsayed.gamescheap.repository.GameDetailsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GameDetailsViewModel(private val gameDetailsRepository: GameDetailsRepository): ViewModel() {

    private val errorHandler = CoroutineExceptionHandler{_, t ->
        run{
            requestStatus.postValue(t.message.toString())
        }
    }

    fun startGameDetailsRequest(){
        viewModelScope.launch(Dispatchers.IO+errorHandler) {
            gameDetailsRepository.getGameDetails()
        }
    }

    val gameDetails: LiveData<String> get() = gameDetailsRepository.gameDetails
    val requestStatus get() = gameDetailsRepository.requestStatus


}