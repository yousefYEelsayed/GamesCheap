package com.yousefelsayed.gamescheap.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Html
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.api.ApiUtilities
import com.yousefelsayed.gamescheap.databinding.ActivityGameDetailsBinding
import com.yousefelsayed.gamescheap.model.GameDetailsModel
import com.yousefelsayed.gamescheap.repository.GameDetailsRepository
import com.yousefelsayed.gamescheap.viewmodel.GameDetailsViewModel
import com.yousefelsayed.gamescheap.viewmodel.GameDetailsViewModelFactory
import org.json.JSONObject


class GameDetailsActivity : AppCompatActivity(),View.OnLongClickListener {

    //Intent data
    private lateinit var gameId:String
    private lateinit var storeId:String
    private lateinit var gameCurrentPrice:String
    private lateinit var gameOldPrice:String
    private lateinit var dealID:String
    //Backend
    private lateinit var gameDetailsRepository: GameDetailsRepository
    private lateinit var gameDetailsViewModel: GameDetailsViewModel
    //Design
    private lateinit var view: ActivityGameDetailsBinding
    //Ads
    private val adRequest: AdRequest = AdRequest.Builder().build()
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = DataBindingUtil.setContentView(this,R.layout.activity_game_details)
        checkForInternetAndUpdateView()
        init()
        showAds()
        updateIconsColors()
        setUpObservers()
        getGameData()
        setupListener()
    }

    private fun init(){
        window.setBackgroundDrawable(null)
        //Get Data to start getting info
        gameId = intent.getStringExtra("gameId").toString()
        storeId = intent.getStringExtra("storeId").toString()
        gameCurrentPrice = intent.getStringExtra("currentPrice").toString()
        gameOldPrice = intent.getStringExtra("oldPrice").toString()
        dealID = intent.getStringExtra("dealId").toString()
        //BottomSheet minimum height size
        view.gameDetailsImageView.doOnLayout {
            BottomSheetBehavior.from(view.bottomSheetLayout).apply {
                peekHeight = view.mainConstraintLayout.height.minus(it.height.plus(24))
            }
        }
        //Backend
        val apiInterface = ApiUtilities.getInstance().create(ApiInterface::class.java)
        gameDetailsRepository = GameDetailsRepository(apiInterface,gameId)
        gameDetailsViewModel = ViewModelProvider(this,GameDetailsViewModelFactory(gameDetailsRepository))[GameDetailsViewModel::class.java]
    }
    private fun updateIconsColors() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                view.exitImageView.setImageResource(R.drawable.ic_baseline_arrow_back_ios_new_24_white)
                view.errorImageView.setImageResource(R.drawable.ic_baseline_error_outline_24_white)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                view.exitImageView.setImageResource(R.drawable.ic_baseline_arrow_back_ios_new_24_black)
                view.errorImageView.setImageResource(R.drawable.ic_baseline_error_outline_24_black)
            }
        }
    }
    private fun checkForInternetAndUpdateView(){
        if (!checkForInternet()){
            Toast.makeText(this,"Check your internet connection", Toast.LENGTH_SHORT).show()
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

    private fun setUpObservers(){
        gameDetailsViewModel.requestStatus.observe(this){
            if (it == "SUCCESS"){
                parseJsonData(gameDetailsViewModel.gameDetails.value)
            }else if(it != ""){
                showErrorScreen()
            }
        }
    }
    private fun getGameData(){
        if (checkForInternet()){
            gameDetailsViewModel.startGameDetailsRequest()
        }
    }
    private fun parseJsonData(jsonResponse: String?) {
        //ParseJson to update view
        val gson = Gson()
        val mainJsonObject = JSONObject(jsonResponse.toString())
        val gameJsonObject: JSONObject = mainJsonObject.getJSONObject(gameId)
        if (gameJsonObject.getBoolean("success")){
            val gameDetailsModel:GameDetailsModel = gson.fromJson(gameJsonObject.getJSONObject("data").toString(),GameDetailsModel::class.java)
            updateViewWithData(gameDetailsModel)
        }else {
            showErrorScreen()
        }
    }
    private fun showErrorScreen() {
        //Hide Loading screen and enable browser button
        view.layoutBottomSheetInclude.openDeal.isClickable = true
        view.loadingRelativeLayout.visibility = View.GONE
        view.errorRelativeLayout.visibility = View.VISIBLE
    }
    private fun updateViewWithData(model:GameDetailsModel){
        //Load Images
        Glide.with(this).load(model.header_image).into(view.gameDetailsImageView)
        Glide.with(this).load(getStoreImageLink(storeId)).into(view.layoutBottomSheetInclude.gameStoreImageView)
        //Update All TextViews
        view.layoutBottomSheetInclude.gameTitle.text = model.name
        view.layoutBottomSheetInclude.gameDescription.text = model.short_description
        if (gameCurrentPrice == "0.00") view.layoutBottomSheetInclude.gameCurrentPrice.text = getString(R.string.Free_String)
        else view.layoutBottomSheetInclude.gameCurrentPrice.text = getString(R.string.priceText,gameCurrentPrice)
        view.layoutBottomSheetInclude.gameOldPrice.text = getString(R.string.priceText,gameOldPrice)
        view.layoutBottomSheetInclude.gameOldPrice.paintFlags = view.layoutBottomSheetInclude.gameOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        if(model.recommendations?.total != null) view.layoutBottomSheetInclude.ratingTextView.text = model.recommendations.total.toString()
        Log.d("Debug","Legal Notice: "+model.legal_notice)
        try {
            view.layoutBottomSheetInclude.gameLegalNotice.text =if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(model.legal_notice, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(model.legal_notice)
            }
        }catch (e:Exception){
            view.layoutBottomSheetInclude.gameLegalNotice.text = ""
        }
        view.layoutBottomSheetInclude.gameMinimumRequirements.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(model.pc_requirements.minimum, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(model.pc_requirements.minimum)
        }

        view.layoutBottomSheetInclude.categorieTextView.text = model.genres[0].description

        //Remove Loading Layout
        view.layoutBottomSheetInclude.openDeal.isClickable = true
        view.loadingRelativeLayout.visibility = View.GONE
    }
    private fun getStoreImageLink(storeID: String):String{
        when (storeID) {
            "1" -> return "https://www.cheapshark.com${ApiUtilities.STORE_1}"
            "2" -> return "https://www.cheapshark.com${ApiUtilities.STORE_2}"
            "3" -> return "https://www.cheapshark.com${ApiUtilities.STORE_3}"
            "4" -> return "https://www.cheapshark.com${ApiUtilities.STORE_4}"
            "5" -> return "https://www.cheapshark.com${ApiUtilities.STORE_5}"
            "6" -> return "https://www.cheapshark.com${ApiUtilities.STORE_6}"
            "7" -> return "https://www.cheapshark.com${ApiUtilities.STORE_7}"
            "8" -> return "https://www.cheapshark.com${ApiUtilities.STORE_8}"
            "9" -> return "https://www.cheapshark.com${ApiUtilities.STORE_9}"
            "10" -> return "https://www.cheapshark.com${ApiUtilities.STORE_10}"
            "11" -> return "https://www.cheapshark.com${ApiUtilities.STORE_11}"
            "12" -> return "https://www.cheapshark.com${ApiUtilities.STORE_12}"
            "13" -> return "https://www.cheapshark.com${ApiUtilities.STORE_13}"
            "14" -> return "https://www.cheapshark.com${ApiUtilities.STORE_14}"
            "15" -> return "https://www.cheapshark.com${ApiUtilities.STORE_15}"
            "16" -> return "https://www.cheapshark.com${ApiUtilities.STORE_16}"
            "17" -> return "https://www.cheapshark.com${ApiUtilities.STORE_17}"
            "18" -> return "https://www.cheapshark.com${ApiUtilities.STORE_18}"
            "19" -> return "https://www.cheapshark.com${ApiUtilities.STORE_19}"
            "20" -> return "https://www.cheapshark.com${ApiUtilities.STORE_20}"
            "21" -> return "https://www.cheapshark.com${ApiUtilities.STORE_21}"
            "22" -> return "https://www.cheapshark.com${ApiUtilities.STORE_22}"
            "23" -> return "https://www.cheapshark.com${ApiUtilities.STORE_23}"
            "24" -> return "https://www.cheapshark.com${ApiUtilities.STORE_24}"
            "25" -> return "https://www.cheapshark.com${ApiUtilities.STORE_25}"
            "26" -> return "https://www.cheapshark.com${ApiUtilities.STORE_26}"
            "27" -> return "https://www.cheapshark.com${ApiUtilities.STORE_27}"
            "28" -> return "https://www.cheapshark.com${ApiUtilities.STORE_28}"
            "29" -> return "https://www.cheapshark.com${ApiUtilities.STORE_29}"
            "30" -> return "https://www.cheapshark.com${ApiUtilities.STORE_30}"
            "31" -> return "https://www.cheapshark.com${ApiUtilities.STORE_31}"
            "32" -> return "https://www.cheapshark.com${ApiUtilities.STORE_32}"
            "33" -> return "https://www.cheapshark.com${ApiUtilities.STORE_33}"
            else -> return "https://www.cheapshark.com${ApiUtilities.STORE_1}"
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupListener(){
        view.layoutBottomSheetInclude.openDeal.setOnClickListener{
            view.layoutBottomSheetInclude.openDeal.isClickable = false
            Snackbar.make(view.mainConstraintLayout,"One Moment Please...",Snackbar.LENGTH_SHORT).show()
            val finalLink: String = ApiUtilities.CHEAP_SHARK_REDIRECT_LINK+dealID
            view.gameWebView.settings.javaScriptEnabled = true
            //Doing this because i don't want to open cheapShark domain on user phone
            view.gameWebView.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                    finish()
                    return false
                }
            }
            view.gameWebView.loadUrl(finalLink)
        }
        view.exitImageView.setOnClickListener {
            finish()
        }
        BottomSheetBehavior.from(view.bottomSheetLayout).addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    view.bottomSheetLayout.setBackgroundResource(R.color.backgroundColor)
                }else{
                    view.bottomSheetLayout.setBackgroundResource(R.drawable.game_bottom_sheet_background)
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        view.layoutBottomSheetInclude.reviewLinearLayout.setOnLongClickListener(this)
        view.layoutBottomSheetInclude.gameCurrentPriceLinearLayout.setOnLongClickListener(this)
        view.layoutBottomSheetInclude.gameOldPriceLinearLayout.setOnLongClickListener(this)
        view.layoutBottomSheetInclude.gameCategoryLinearLayout.setOnLongClickListener(this)
    }
    override fun onLongClick(v: View?): Boolean {
        if (v?.tag.toString().isNotEmpty() && v?.tag.toString() != "null"){
            showSnackBar(v?.tag.toString())
        }
        return true
    }
    private fun showSnackBar(msg:String){
        Snackbar.make(view.mainConstraintLayout,msg,Snackbar.LENGTH_SHORT).show()
        vibrateDevice()
    }
    private fun vibrateDevice(){
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) { // Vibrator availability checking
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)) // New vibrate method for API Level 26 or higher
            } else {
                vibrator.vibrate(500) // Vibrate method for below API Level 26
            }
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
                mInterstitialAd?.show(this@GameDetailsActivity)
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