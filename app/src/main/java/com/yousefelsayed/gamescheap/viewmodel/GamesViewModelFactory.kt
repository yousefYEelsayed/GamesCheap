package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yousefelsayed.gamescheap.repository.GamesRepository
import javax.inject.Inject

class GamesViewModelFactory @Inject constructor(private val gamesRepository: GamesRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GamesViewModel(gamesRepository) as T
    }
}