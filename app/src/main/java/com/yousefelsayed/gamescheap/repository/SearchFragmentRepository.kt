package com.yousefelsayed.gamescheap.repository

import android.util.Log
import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.api.ApiUtilities
import com.yousefelsayed.gamescheap.model.SearchModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchFragmentRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun getSearchResults(searchQuery:String): Flow<SearchModel>{
        Log.d("Debug","Search URL: "+ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_SEARCH+searchQuery)
        return flow {
            emit(apiInterface.getSearchResults(ApiUtilities.CHEAP_SHARK_BASE_URL + ApiUtilities.CHEAP_SHARK_SEARCH + searchQuery))
        }.map {
            it.body()!!
        }
    }

}