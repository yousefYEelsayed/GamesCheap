package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yousefelsayed.gamescheap.repository.GameDetailsRepository

class GameDetailsViewModelFactory(private val gameDetailsRepository: GameDetailsRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameDetailsViewModel(gameDetailsRepository) as T
    }

}