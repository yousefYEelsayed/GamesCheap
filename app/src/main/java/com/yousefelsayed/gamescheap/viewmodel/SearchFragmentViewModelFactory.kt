package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yousefelsayed.gamescheap.repository.SearchFragmentRepository
import javax.inject.Inject

class SearchFragmentViewModelFactory @Inject constructor(private val searchFragmentRepository: SearchFragmentRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchFragmentViewModel(searchFragmentRepository) as T
    }
}