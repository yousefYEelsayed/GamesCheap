package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yousefelsayed.gamescheap.repository.SearchRepository
import javax.inject.Inject

class SearchViewModelFactory @Inject constructor(private val searchRepository: SearchRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(searchRepository) as T
    }
}