package com.yousefelsayed.gamescheap.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.api.ApiUtilities
import com.yousefelsayed.gamescheap.model.StoresModel
import javax.inject.Inject

class StoresRepository @Inject constructor(private val apiInterface:ApiInterface) {

    private val storeLiveData = MutableLiveData<StoresModel>()
    val storesLiveData:LiveData<StoresModel> get() = storeLiveData
    val requestStatus = MutableLiveData("")

    suspend fun getStoresData(){
        val result = apiInterface.getStores(ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_STORES_URL)
        if (result.body() != null){
            storeLiveData.postValue(result.body())
            requestStatus.postValue("SUCCESS")
        }
    }
}