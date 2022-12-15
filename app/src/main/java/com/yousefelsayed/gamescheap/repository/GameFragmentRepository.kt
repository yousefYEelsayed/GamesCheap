package com.yousefelsayed.gamescheap.repository

import android.util.Log
import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.api.ApiUtilities
import com.yousefelsayed.gamescheap.model.Games
import com.yousefelsayed.gamescheap.model.StoresModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameFragmentRepository@Inject constructor(private val apiInterface: ApiInterface) {

    var pagesMax :String = "50"

    suspend fun getGamesDeals(storesID:String,maxValue:String,pageNumber:String,sortMode:String): Flow<Games>{
        if (storesID != "ALL") {
            Log.d("Debug","Not All: "+ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_STORES_DEALS_URL+"&storeID="+storesID+"&upperPrice=$maxValue"+"&pageNumber=$pageNumber&sortBy=$sortMode")
            return flow {
                val result = apiInterface.getGameDeals(ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_STORES_DEALS_URL+"&storeID="+storesID+"&upperPrice=$maxValue"+"&pageNumber=$pageNumber&sortBy=$sortMode")
                pagesMax = result.headers().get("X-Total-Page-Count")!!
                emit(result)
            }.map {
                it.body()!!
            }
        }else{
            Log.d("Debug", "All: " + ApiUtilities.CHEAP_SHARK_BASE_URL + ApiUtilities.CHEAP_SHARK_STORES_DEALS_URL + "upperPrice=$maxValue" + "&pageNumber=$pageNumber&sortBy=$sortMode")
            return flow {
                val result =
                    apiInterface.getGameDeals(ApiUtilities.CHEAP_SHARK_BASE_URL + ApiUtilities.CHEAP_SHARK_STORES_DEALS_URL + "upperPrice=$maxValue" + "&pageNumber=$pageNumber&sortBy=$sortMode")
                pagesMax = result.headers().get("X-Total-Page-Count")!!
                emit(result)
            }.map {
                it.body()!!
            }
        }
    }
    suspend fun getStoresData(): Flow<StoresModel>{
        return flow {
            emit(apiInterface.getStores(ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_STORES_URL))
        }.map {
            it.body()!!
        }
    }

}