package com.yousefelsayed.gamescheap.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.yousefelsayed.gamescheap.R

class FilterProfile(): ViewModel() {
    //To Prevent continuous ui updates
    var shouldUpdateView = false
    //Stores
    var totalStores: Int = 0
    var totalSelectedStores: Int = -1
    var selectedStores :String = ""
    var checkedStoresIds: List<Int>? = null
    //Filter Data
    var currentGamePriceFilter: String = "500"
    var currentGamesPageNumber: Int = 0
    var sortMode: String = "deal rating"
    var checkedSortId: Int = R.id.dealRating

    fun addStoreToString(storeId:Int){
        if (selectedStores.isEmpty()){
            selectedStores = storeId.toString()
        }else{
            selectedStores += ",$storeId"
        }
        totalSelectedStores++
    }
    fun removeStoreFromString(storeId:Int){
        var resultString = ""
        for (i in selectedStores.split(",")){
            if(resultString.isEmpty() && i != storeId.toString()){
                resultString = i
            }else if (resultString.isNotEmpty() && i != storeId.toString()){
                resultString += ",$i"
            }
        }
        totalSelectedStores--
        selectedStores = resultString
        Log.d("Debug","Stores Total: $totalStores, Current Selected Stores: $totalSelectedStores, Stores ID: $selectedStores")
    }
}