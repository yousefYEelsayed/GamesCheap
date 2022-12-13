package com.yousefelsayed.gamescheap.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.api.ApiUtilities
import com.yousefelsayed.gamescheap.databinding.ActivityWebViewBinding


class WebViewActivity : AppCompatActivity() {

    //Intent
    private lateinit var dealId:String
    //Views
    private lateinit var view: ActivityWebViewBinding
    //Ads
    private val adRequest: AdRequest = AdRequest.Builder().build()
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = DataBindingUtil.setContentView(this,R.layout.activity_web_view)
        checkForInternetAndUpdateView()
        init()
        showAds()
        setupWebView()
        changeIconsToLightMode()
        setupListeners()
    }
    private fun init(){
        window.setBackgroundDrawable(null)
        dealId = intent.getStringExtra("dealId").toString()
    }
    private fun checkForInternetAndUpdateView(){
        if (!checkForInternet()){
            Toast.makeText(this,"Check your internet connection",Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    @Suppress("DEPRECATION")
    private fun checkForInternet(): Boolean{
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(){
        view.gameWebView.settings.javaScriptEnabled = true
        view.gameWebView.settings.javaScriptCanOpenWindowsAutomatically = false
        view.gameWebView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return false
            }

            override fun onPageFinished(view1: WebView?, url: String?) {
                super.onPageFinished(view1, url)
                if (url!!.contains("cheapshark.com")) {
                    view.loadingRelativeLayout.visibility = View.VISIBLE
                } else {
                    view.loadingRelativeLayout.visibility = View.GONE
                }
            }

            override fun onPageStarted(view1: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view1, url, favicon)
                if (url!!.contains("cheapshark.com")) {
                    view.loadingRelativeLayout.visibility = View.VISIBLE
                } else {
                    view.loadingRelativeLayout.visibility = View.GONE
                }
            }
        }
        if (!checkForInternet()) return
        view.gameWebView.loadUrl(ApiUtilities.CHEAP_SHARK_REDIRECT_LINK+dealId)
    }
    private fun changeIconsToLightMode() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                view.openInBrowser.setImageResource(R.drawable.ic_baseline_open_in_browser_24_white)
                view.exitImageView.setImageResource(R.drawable.ic_baseline_arrow_back_ios_new_24_white)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                view.openInBrowser.setImageResource(R.drawable.ic_baseline_open_in_browser_24_black)
                view.exitImageView.setImageResource(R.drawable.ic_baseline_arrow_back_ios_new_24_black)
            }
        }
    }

    private fun setupListeners(){
        view.openInBrowser.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(view.gameWebView.url))
            startActivity(browserIntent)
            finish()
        }
        view.exitImageView.setOnClickListener {
            finish()
        }
    }
    //Ads
    private fun showAds(){
        if (!checkForInternet()) return
        //InterstitialAd
        InterstitialAd.load(this,getString(R.string.deal_screen_interstitial_ad), adRequest,object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("Debug", "Full screen Ad Error: $adError")
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("Debug", "Ad was loaded.")
                mInterstitialAd = interstitialAd
                setUpAdListener()
                mInterstitialAd?.show(this@WebViewActivity)
            }
        })
    }
    private fun setUpAdListener(){
        //Ads
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
            }
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
            }

            override fun onAdImpression() {
            }

            override fun onAdShowedFullScreenContent() {
            }
        }
    }
    override fun onDestroy() {
        mInterstitialAd = null
        super.onDestroy()
    }
}