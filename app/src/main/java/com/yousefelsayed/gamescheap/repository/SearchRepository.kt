package com.yousefelsayed.gamescheap.repository

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.api.ApiUtilities
import com.yousefelsayed.gamescheap.model.SearchModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchRepository @Inject constructor(private val apiInterface: ApiInterface) {

    private val searchLiveData = MutableLiveData<SearchModel>()
    val search: LiveData<SearchModel> get() = searchLiveData
    val requestStatus = MutableLiveData("")

    suspend fun getSearchResults(searchQuery:String){
        Log.d("Debug","Search URL: "+ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_SEARCH+searchQuery)
        val result = apiInterface.getSearchResults(ApiUtilities.CHEAP_SHARK_BASE_URL + ApiUtilities.CHEAP_SHARK_SEARCH + searchQuery)
        if (result.body() != null) {
            searchLiveData.postValue(result.body())
            requestStatus.postValue("SUCCESS")
        }
    }

}