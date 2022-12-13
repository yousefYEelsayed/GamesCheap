package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yousefelsayed.gamescheap.repository.StoresRepository
import javax.inject.Inject

class StoresViewModelFactory@Inject constructor(private val storesRepository: StoresRepository):ViewModelProvider.Factory {
    override fun<T:ViewModel> create(modelClass: Class<T>): T{
        return StoresViewModel(storesRepository) as T
    }
}