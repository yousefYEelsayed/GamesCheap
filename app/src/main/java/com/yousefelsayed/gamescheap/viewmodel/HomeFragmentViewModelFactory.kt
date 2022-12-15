package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yousefelsayed.gamescheap.repository.HomeFragmentRepository
import javax.inject.Inject

class HomeFragmentViewModelFactory @Inject constructor(private val homeFragmentRepository: HomeFragmentRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeFragmentViewModel(homeFragmentRepository) as T
    }

}