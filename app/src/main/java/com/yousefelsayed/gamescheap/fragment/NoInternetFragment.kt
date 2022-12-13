package com.yousefelsayed.gamescheap.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.databinding.FragmentNoInternetBinding


class NoInternetFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: FragmentNoInternetBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_no_internet,container,false)

        view.retryButton.setOnClickListener{
            checkForInternetAndUpdateView(it)
        }
        return view.root
    }

    private fun checkForInternetAndUpdateView(view: View){
        if (checkForInternet()){
            val action = NoInternetFragmentDirections.actionNoInternetFragmentToHomeFragment()
            view.findNavController().navigate(action)
        }
    }
    @Suppress("DEPRECATION")
    private fun checkForInternet(): Boolean{
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }else {
            val network = connectivityManager.activeNetworkInfo ?: return false
            if (network.type == ConnectivityManager.TYPE_WIFI) return true
            return network.type == ConnectivityManager.TYPE_MOBILE
        }
    }

}