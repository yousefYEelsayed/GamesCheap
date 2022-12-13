package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yousefelsayed.gamescheap.repository.GameDealsRepository
import javax.inject.Inject

class GamesDealsViewModelFactory@Inject constructor(private val gamesDealsRepository: GameDealsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GamesDealsViewModel(gamesDealsRepository) as T
    }
}