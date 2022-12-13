package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yousefelsayed.gamescheap.repository.SteamAndEpicGamesRepository
import javax.inject.Inject

class SteamAndEpicGamesViewModelFactory @Inject constructor(private val steamAndEpicGamesRepository: SteamAndEpicGamesRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SteamAndEpicGamesViewModel(steamAndEpicGamesRepository) as T
    }

}