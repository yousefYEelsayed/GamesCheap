package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yousefelsayed.gamescheap.repository.GameFragmentRepository
import javax.inject.Inject

class GamesFragmentViewModelFactory@Inject constructor(private val gamesDealsRepository: GameFragmentRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GamesFragmentViewModel(gamesDealsRepository) as T
    }
}