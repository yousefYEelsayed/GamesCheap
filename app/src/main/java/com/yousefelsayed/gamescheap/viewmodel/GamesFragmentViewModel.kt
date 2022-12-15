package com.yousefelsayed.gamescheap.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefelsayed.gamescheap.api.Resource
import com.yousefelsayed.gamescheap.model.Games
import com.yousefelsayed.gamescheap.model.StoresModel
import com.yousefelsayed.gamescheap.repository.GameFragmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class GamesFragmentViewModel(private val gamesDealsRepository: GameFragmentRepository): ViewModel(){

    private val _gamesDealsData :MutableStateFlow<Resource<Games>> = MutableStateFlow(Resource.loading())
    val gamesDealsData :MutableStateFlow<Resource<Games>> get() = _gamesDealsData
    private val _storesData :MutableStateFlow<Resource<StoresModel>> = MutableStateFlow(Resource.loading())
    val storesData :MutableStateFlow<Resource<StoresModel>> get() = _storesData
    val pagesMaxValue :String get() = gamesDealsRepository.pagesMax

    fun startGamesRequest(storesID:String,maxPrice:String,pageNumber:String,sortMode:String){
        viewModelScope.launch(Dispatchers.IO) {
            gamesDealsRepository.getGamesDeals(storesID,maxPrice,pageNumber,sortMode).catch { error ->
                _gamesDealsData.value = Resource.error(error.message!!)
            }.collect{ games ->
                _gamesDealsData.value = Resource.success(games)
            }
        }
    }
    fun getStores(){
        viewModelScope.launch(Dispatchers.IO) {
            gamesDealsRepository.getStoresData().catch { error ->
                _storesData.value = Resource.error(error.message!!)
            }.collect{ stores ->
                _storesData.value = Resource.success(stores)
            }
        }
    }
}