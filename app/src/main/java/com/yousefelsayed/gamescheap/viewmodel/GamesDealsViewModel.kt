package com.yousefelsayed.gamescheap.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefelsayed.gamescheap.model.Games
import com.yousefelsayed.gamescheap.repository.GameDealsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class GamesDealsViewModel(private val gamesDealsRepository: GameDealsRepository): ViewModel(){
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, t ->
        run {
            requestStatus.postValue(t.message.toString())
        }
    }
    fun startGamesRequest(storesID:String,maxPrice:String,pageNumber:String,sortMode:String){
        viewModelScope.launch(Dispatchers.IO+coroutineExceptionHandler) {
            gamesDealsRepository.getGamesDeals(storesID,maxPrice,pageNumber,sortMode)
        }
    }

    val gamesDealsData :LiveData<Games> get() = gamesDealsRepository.games
    val pagesMaxValue :String get() = gamesDealsRepository.pagesMax
    val requestStatus: MutableLiveData<String> get() = gamesDealsRepository.requestStatus
}