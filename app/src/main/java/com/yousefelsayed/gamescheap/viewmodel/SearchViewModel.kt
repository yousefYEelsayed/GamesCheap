package com.yousefelsayed.gamescheap.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefelsayed.gamescheap.model.SearchModel
import com.yousefelsayed.gamescheap.repository.SearchRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SearchViewModel(private val searchRepository: SearchRepository): ViewModel() {

    private val errorHandler = CoroutineExceptionHandler{_, t ->
        run{
            requestStatus.postValue(t.message.toString())
        }
    }
    fun startSearch(searchQuery: String){
        viewModelScope.launch(Dispatchers.IO+errorHandler) {
            searchRepository.getSearchResults(searchQuery)
        }
    }

    val searchData: LiveData<SearchModel> get() = searchRepository.search
    val requestStatus get() = searchRepository.requestStatus
}