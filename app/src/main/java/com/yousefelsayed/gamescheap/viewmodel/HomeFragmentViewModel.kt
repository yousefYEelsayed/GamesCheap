package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefelsayed.gamescheap.api.Resource
import com.yousefelsayed.gamescheap.model.Games
import com.yousefelsayed.gamescheap.repository.HomeFragmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class HomeFragmentViewModel(private val homeFragmentRepository: HomeFragmentRepository): ViewModel(){

    fun startSteamEpicGamesRequest(){
        viewModelScope.launch(Dispatchers.IO) {
            homeFragmentRepository.getSteamAndEpicGames()
                .catch { error ->
                    _steamEpicGames.value = Resource.error(error.message!!)
                }
                .collect{ result ->
                    _steamEpicGames.value = Resource.success(result)
            }
        }
    }
    fun startTopGamesRequest(){
        viewModelScope.launch(Dispatchers.IO) {
            homeFragmentRepository.getTopGames().catch { error ->
                _topGames.value = Resource.error(error.message!!)
            }.collect{ result ->
                _topGames.value = Resource.success(result)
            }
        }
    }
    private val _topGames = MutableStateFlow<Resource<Games>>(Resource.loading())
    val topGames get() = _topGames
    private val _steamEpicGames = MutableStateFlow<Resource<Games>>(Resource.loading())
    val steamEpicGames get() = _steamEpicGames
}
