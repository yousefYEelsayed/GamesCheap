package com.yousefelsayed.gamescheap.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.api.ApiUtilities
import com.yousefelsayed.gamescheap.model.Games
import javax.inject.Inject

class GameDealsRepository@Inject constructor(private val apiInterface: ApiInterface) {

    private val gamesLiveData = MutableLiveData<Games>()
    val games :LiveData<Games> get() = gamesLiveData
    var pagesMax :String = "50"
    var requestStatus = MutableLiveData("")

    suspend fun getGamesDeals(storesID:String,maxValue:String,pageNumber:String,sortMode:String){
        if (storesID == "ALL"){
            Log.d("Debug","All: "+ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_STORES_DEALS_URL+"upperPrice=$maxValue"+"&pageNumber=$pageNumber&sortBy=$sortMode")
            val result = apiInterface.getGameDeals(ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_STORES_DEALS_URL+"upperPrice=$maxValue"+"&pageNumber=$pageNumber&sortBy=$sortMode")
            if (result.body() != null) {
                Log.d("Debug", "PageMax: ${result.headers().get("X-Total-Page-Count")}")
                pagesMax = result.headers().get("X-Total-Page-Count")!!
                gamesLiveData.postValue(result.body())
                requestStatus.postValue("SUCCESS")
            }
        }else if (storesID != "ALL"){
            Log.d("Debug","Not All: "+ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_STORES_DEALS_URL+"&storeID="+storesID+"&upperPrice=$maxValue"+"&pageNumber=$pageNumber&sortBy=$sortMode")
            val result = apiInterface.getGameDeals(ApiUtilities.CHEAP_SHARK_BASE_URL+ApiUtilities.CHEAP_SHARK_STORES_DEALS_URL+"&storeID="+storesID+"&upperPrice=$maxValue"+"&pageNumber=$pageNumber&sortBy=$sortMode")
            if (result.body() != null){
                Log.d("Debug","PageMax: ${result.headers().get("X-Total-Page-Count")}")
                pagesMax = result.headers().get("X-Total-Page-Count")!!
                gamesLiveData.postValue(result.body())
                requestStatus.postValue("SUCCESS")
            }
        }
    }

}