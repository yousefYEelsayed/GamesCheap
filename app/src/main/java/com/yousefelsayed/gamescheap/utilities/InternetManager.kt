package com.yousefelsayed.gamescheap.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class InternetManager() {
    private lateinit var c:Context
    constructor(c:Context) : this() {
        this.c = c
   }
    private val connectivityManager = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    fun checkForInternet(): Boolean{
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}