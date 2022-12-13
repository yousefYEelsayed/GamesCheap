package com.yousefelsayed.gamescheap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefelsayed.gamescheap.model.StoresModel
import com.yousefelsayed.gamescheap.repository.StoresRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StoresViewModel(private val storesRepository: StoresRepository): ViewModel(){

    private val errorHandler = CoroutineExceptionHandler{_, t ->
        run{
            requestStatus.postValue(t.message.toString())
        }
    }

    fun getStores(){
        viewModelScope.launch(Dispatchers.IO+errorHandler) {
            storesRepository.getStoresData()
        }
    }

    val stores: LiveData<StoresModel> get() = storesRepository.storesLiveData
    val requestStatus get() = storesRepository.requestStatus


}