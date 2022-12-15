package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefelsayed.gamescheap.api.Resource
import com.yousefelsayed.gamescheap.model.SearchModel
import com.yousefelsayed.gamescheap.repository.SearchFragmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SearchFragmentViewModel(private val searchFragmentRepository: SearchFragmentRepository): ViewModel() {

    private val _searchData : MutableStateFlow<Resource<SearchModel>> = MutableStateFlow(Resource.loading())
    val searchData : MutableStateFlow<Resource<SearchModel>> get() = _searchData

    fun startSearch(searchQuery: String){
        viewModelScope.launch(Dispatchers.IO) {
            searchFragmentRepository.getSearchResults(searchQuery).catch { error ->
                _searchData.value = Resource.error(error.message!!)
            }.collect{ result ->
                _searchData.value = Resource.success(result)
            }
        }
    }
}