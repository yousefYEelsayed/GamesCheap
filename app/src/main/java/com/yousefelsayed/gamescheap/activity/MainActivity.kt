package com.yousefelsayed.gamescheap.activity

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.google.android.gms.ads.MobileAds
import com.yousefelsayed.gamescheap.BaseApplication
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.databinding.ActivityMainBinding
import com.yousefelsayed.gamescheap.fragment.GamesFragment
import com.yousefelsayed.gamescheap.fragment.HomeFragment
import com.yousefelsayed.gamescheap.fragment.SearchFragment
import com.yousefelsayed.gamescheap.utilities.InternetManager
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity


class MainActivity : AppCompatActivity() {

    //Views
    private lateinit var view: ActivityMainBinding
    private lateinit var navController: NavController
    //Backend
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()
        setupLightMode(sp.getInt("DarkMode",0))
        setupListeners()

    }

    private fun init(){
        window.setBackgroundDrawable(null)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentView) as NavHostFragment
        navController = navHostFragment.findNavController()
        navController.setGraph(R.navigation.nav)
        sp = getSharedPreferences("GamesCheap",0)
        //setup bottomNav
        NavigationUI.setupWithNavController(view.bottomNav,navController,false)

        //check for darkMode to setupValues, Default value is 3 to make sure if it's app first run
        if (sp.getInt("DarkMode",3) == 3){
            Log.d("Debug","FirstAppRun")
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    view.lightModeImageView.setImageResource(R.drawable.ic_baseline_dark_mode_24)
                    sp.edit().putInt("DarkMode",1).apply()
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    view.lightModeImageView.setImageResource(R.drawable.ic_baseline_wb_sunny_24)
                    sp.edit().putInt("DarkMode",0).apply()
                }
            }
        }
        //Ads
        MobileAds.initialize(this@MainActivity) {
            Log.d("Ads","Ads Init Status: $it")
        }
    }
    private fun setupListeners(){
        view.lightModeImageView.setOnClickListener{
            Log.d("Debug","ImageClicked")
            changeLightMode(sp.getInt("DarkMode",0))
        }
        startNetworkChangeListener()
    }
    private fun startNetworkChangeListener(){
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        //There's Internet
                        Log.d("Debug","Internet is back")
                        if (navController.currentDestination?.id == R.id.noInternetFragment){
                            runOnUiThread {
                                navController.navigate(R.id.homeFragment)
                            }
                        }
                    }
                    override fun onLost(network: Network) {
                        runOnUiThread {
                            navController.navigate(R.id.noInternetFragment)
                        }
                        Log.d("Debug","Internet is lost")
                    }
                })
            }else {
                val builder = NetworkRequest.Builder()
                builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                it.registerNetworkCallback(builder.build(),object: ConnectivityManager.NetworkCallback(){
                    override fun onAvailable(network: Network) {
                        //There's Internet
                        Log.d("Debug","Internet is back")
                        if (navController.currentDestination?.id == R.id.noInternetFragment){
                            runOnUiThread {
                                navController.navigate(R.id.homeFragment)
                            }
                        }
                    }
                    override fun onLost(network: Network) {
                        runOnUiThread {
                            navController.navigate(R.id.noInternetFragment)
                        }
                        Log.d("Debug","Internet is lost")
                    }
                })
            }
        }
    }

    private fun changeLightMode(darkMode: Int){
        Log.d("Debug","ChangeLightFunCalled")
        if (darkMode == 0){
            view.lightModeImageView.setImageResource(R.drawable.ic_baseline_dark_mode_24)
            sp.edit().putInt("DarkMode",1).apply()
            view.bottomNav.selectedItemId = R.id.homeFragment
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        }else if(darkMode == 1) {
            view.lightModeImageView.setImageResource(R.drawable.ic_baseline_wb_sunny_24)
            sp.edit().putInt("DarkMode",0).apply()
            view.bottomNav.selectedItemId = R.id.homeFragment
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }
    }
    private fun setupLightMode(darkMode: Int){
        if (darkMode == 0){
            view.lightModeImageView.setImageResource(R.drawable.ic_baseline_wb_sunny_24)
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }else if(darkMode == 1) {
            view.lightModeImageView.setImageResource(R.drawable.ic_baseline_dark_mode_24)
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        }
    }
    private fun updateIcon(darkMode: Int){
        if(darkMode == 1){
            //Enable DarkMode Icon
            packageManager.setComponentEnabledSetting(ComponentName(packageName,"$packageName.activity.MainActivity"),PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP)
            packageManager.setComponentEnabledSetting(ComponentName(packageName,"$packageName.darkModeLogo"),PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP)

        }else {
            //Disable DarkMode Icon
            packageManager.setComponentEnabledSetting(ComponentName(packageName,"$packageName.activity.MainActivity"),PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP)
            packageManager.setComponentEnabledSetting(ComponentName(packageName,"$packageName.darkModeLogo"),PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP)
        }
    }

    //Handel selected item in bottom-nav after device rotation
    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putInt("BOTTOM_NAV_LAST_SELECTED", view.bottomNav.selectedItemId)
        }
        super.onSaveInstanceState(outState)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        view.bottomNav.selectedItemId = savedInstanceState.getInt("BOTTOM_NAV_LAST_SELECTED")
    }
    
    override fun onDestroy() {
        //updateIcon(sp.getInt("DarkMode",0))
        view.unbind()
        Thread{
            Glide.get(this).clearDiskCache()
        }
        super.onDestroy()
    }

}