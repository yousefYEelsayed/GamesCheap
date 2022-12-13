package com.yousefelsayed.gamescheap.fragment

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.activity.GameDetailsActivity
import com.yousefelsayed.gamescheap.activity.WebViewActivity
import com.yousefelsayed.gamescheap.adapter.GamesRecyclerAdapter
import com.yousefelsayed.gamescheap.databinding.FragmentGamesBinding
import com.yousefelsayed.gamescheap.viewmodel.FilterProfile
import com.yousefelsayed.gamescheap.model.Games
import com.yousefelsayed.gamescheap.model.GamesItem
import com.yousefelsayed.gamescheap.model.StoresModel
import com.yousefelsayed.gamescheap.viewmodel.*
import dagger.android.support.DaggerFragment
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.random.Random

class GamesFragment : DaggerFragment() {

    //View
    private var _view: FragmentGamesBinding? = null
    private val view get() = _view!!
    //Filter Data
    private lateinit var filterViewModel: FilterProfile
    //Backend
    @Inject
    lateinit var storesViewModelFactory: StoresViewModelFactory
    private var _storesViewModel: StoresViewModel? = null
    private val storesViewModel: StoresViewModel get() = _storesViewModel!!
    private lateinit var storesModel: StoresModel
    @Inject
    lateinit var gamesDealsViewModelFactory: GamesDealsViewModelFactory
    private var _gamesDealsViewModel: GamesDealsViewModel? = null
    private val gamesDealsViewModel: GamesDealsViewModel get() = _gamesDealsViewModel!!
    //Filter Data
    private var addingNewItemsToGamesRecycler:Boolean = false
    //RecyclerView
    private var gamesRecyclerArray: ArrayList<GamesItem> = ArrayList()
    private var gamesRecyclerAdapter: GamesRecyclerAdapter? = null
    //Ads
    private val adRequest: AdRequest = AdRequest.Builder().build()
    private var mInterstitialAd: InterstitialAd? = null
    //Screen Orientation
    private var itemsPerRow = 2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _view  = DataBindingUtil.inflate(inflater,R.layout.fragment_games,container,false)
        init()
        checkScreenOrientation()
        setUpListeners()
        setUpObservers()
        getStoresList()
        return view.root
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

    private fun init(){
        _gamesDealsViewModel = ViewModelProvider(this@GamesFragment,gamesDealsViewModelFactory)[GamesDealsViewModel::class.java]
        _storesViewModel = ViewModelProvider(this@GamesFragment,storesViewModelFactory)[StoresViewModel::class.java]
        filterViewModel = ViewModelProvider(requireActivity())[FilterProfile::class.java]
    }
    private fun setUpListeners(){
        //Filter Listeners
        //OnChangeListener to update UI for better UX feeling
        view.layoutBottomSheetInclude.priceSlider.addOnChangeListener { _, value, _ ->
            view.layoutBottomSheetInclude.currentPriceFilter.text = getString(R.string.priceText,value.roundToInt().toString())
            filterViewModel.currentGamePriceFilter = value.roundToInt().toString()
        }
        //OnTouchListener to send only 1 request to the api
        view.layoutBottomSheetInclude.priceSlider.addOnSliderTouchListener(object :Slider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
                view.resultsRecyclerViewLayout.visibility = View.INVISIBLE
                startShimmerLayout(view.gamesShimmerLayout)
                clearRecyclerDataAndStartGamesRequest()
            }
        })
        view.layoutBottomSheetInclude.priceSlider.setLabelFormatter { value: Float ->
            val format = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 0
            format.currency = Currency.getInstance("USD")
            format.format(value.toDouble())
        }
        view.layoutBottomSheetInclude.sortChipGroupLayout.setOnCheckedStateChangeListener { _, checkedIds ->
            filterViewModel.sortMode = if (checkedIds.size > 0){
                val selectedChip: Chip? = getView()?.findViewById(checkedIds[0])
                Log.d("Debug","Selected chip: ${selectedChip?.tag}")
                "${selectedChip?.tag}"
            }else {
                "deal rating"
            }
            filterViewModel.checkedSortId = view.layoutBottomSheetInclude.sortChipGroupLayout.checkedChipId
            view.resultsRecyclerViewLayout.visibility = View.INVISIBLE
            showViewWithAnimation(view.gamesShimmerLayout)
            clearRecyclerDataAndStartGamesRequest()
        }
        //Views Listeners
        view.filterButton.setOnClickListener{
            if (Random.nextInt(0,3) == 2) run {
                mInterstitialAd?.show(requireActivity())
                setUpAdsListener()
            }
            showFilterBottomSheet()
        }
        view.resultsRecyclerViewLayout.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)){
                    if (addingNewItemsToGamesRecycler || !checkForInternet()) return
                    if (gamesDealsViewModel.pagesMaxValue.toInt() > filterViewModel.currentGamesPageNumber){
                        //Scroll To last item for better UX
                        recyclerView.scrollToPosition(gamesRecyclerArray.size - 1)
                        showLoadingProgressBar()
                        //Because i am using the same observer for 2 types of data handel
                        addingNewItemsToGamesRecycler = true
                        filterViewModel.currentGamesPageNumber++
                        startGamesDealsRequest()
                    }
                }
            }
        })
        view.serverErrorLayoutInclude.serverErrorRetryButton.setOnClickListener{
            view.resultsRecyclerViewLayout.visibility = View.INVISIBLE
            startShimmerLayout(view.gamesShimmerLayout)
            startGamesDealsRequest()
            hideViewWithAnimation(view.zeroFoundLayout)
        }
        BottomSheetBehavior.from(view.bottomSheetLayout).addBottomSheetCallback(object: BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (filterViewModel.shouldUpdateView && newState == 2){
                    view.layoutBottomSheetInclude.priceSlider.value = filterViewModel.currentGamePriceFilter.toFloat()
                    view.layoutBottomSheetInclude.currentPriceFilter.text = getString(R.string.priceText,filterViewModel.currentGamePriceFilter)
                    view.layoutBottomSheetInclude.sortChipGroupLayout.check(filterViewModel.checkedSortId)
                    if (filterViewModel.checkedStoresIds != null){
                        for (i in filterViewModel.checkedStoresIds!!){
                            view.layoutBottomSheetInclude.storePickChipGroupLayout.check(i)
                        }
                    }

                    filterViewModel.shouldUpdateView = false
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }
    private fun setUpAdsListener(){
        //Ads
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d("Debug", "Ad was clicked.")
            }
            override fun onAdDismissedFullScreenContent() {
                Log.d("Debug", "Ad dismissed fullscreen content.")
                mInterstitialAd = null
                reloadInterstitialAd()
            }

            override fun onAdImpression() {
                Log.d("Debug", "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("Debug", "Ad showed fullscreen content.")
            }
        }
    }
    private fun setUpObservers(){
        storesViewModel.requestStatus.observe(viewLifecycleOwner){
            if (it == "SUCCESS"){
                storesModel = storesViewModel.stores.value!!
                setUpStoresChipGroup(storesModel)
                hideViewWithAnimation(view.serverErrorLayoutInclude.serverErrorMainLayout)
            }else if(it != ""){
                showViewWithAnimation(view.serverErrorLayoutInclude.serverErrorMainLayout)
            }
        }
        gamesDealsViewModel.requestStatus.observe(viewLifecycleOwner){
            if (it == "SUCCESS"){
                if (!addingNewItemsToGamesRecycler){
                    setUpGamesRecycler(gamesDealsViewModel.gamesDealsData.value!!)
                }else {
                    setUpNewGamesToRecycler(gamesDealsViewModel.gamesDealsData.value!!)
                }
                hideViewWithAnimation(view.serverErrorLayoutInclude.serverErrorMainLayout)
            }else if(it != ""){
                showViewWithAnimation(view.serverErrorLayoutInclude.serverErrorMainLayout)
                hideLoadingProgressBar()
            }
        }
    }
    private fun checkScreenOrientation(){
        itemsPerRow = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            2
        }else {
            4
        }
        view.shimmerGridLayout.columnCount = itemsPerRow
    }
    //Stores ChipGroup
    private fun getStoresList(){
        if (!checkForInternet()) return
        storesViewModel.getStores()
    }
    private fun setUpStoresChipGroup(data:StoresModel){
        //Creating Chips Group Layout
        val chipGroup = ChipGroup(context)
        chipGroup.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        chipGroup.isSingleSelection = false
        chipGroup.isSingleLine = false
        for (currentModel in data){
            if (currentModel.isActive == 1){
                view.layoutBottomSheetInclude.storePickChipGroupLayout.addView(createChip(currentModel.storeName,currentModel.storeID.toInt()))
                filterViewModel.totalStores++
            }
        }
        view.filterButton.isClickable = true
        startGamesDealsRequest()
    }
    private fun createChip(label: String,storeId: Int): Chip {
        val chip = Chip(context, null, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Entry)
        chip.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        chip.text = label
        chip.tag = storeId.toString()
        chip.id = storeId
        chip.isCloseIconVisible = false
        chip.isChipIconVisible = true
        chip.isCheckable = true
        chip.isClickable = true
        chip.setOnCheckedChangeListener { _, isChecked ->
            view.resultsRecyclerViewLayout.visibility = View.INVISIBLE
            startShimmerLayout(view.gamesShimmerLayout)
            clearGamesRecyclerData()
            // Handle chip checked/unchecked
            if (isChecked){
                filterViewModel.addStoreToString(chip.tag.toString().toInt())
            }else {
                filterViewModel.removeStoreFromString(chip.tag.toString().toInt())
            }
            filterViewModel.checkedStoresIds = view.layoutBottomSheetInclude.storePickChipGroupLayout.checkedChipIds
            startGamesDealsRequest()
        }
        return chip
    }
    //Results RecyclerView
    private fun startGamesDealsRequest(){
        if (!checkForInternet()) return
        if (filterViewModel.totalStores == filterViewModel.totalSelectedStores || filterViewModel.totalSelectedStores == -1){
            Log.d("Debug","1nd if $filterViewModel.totalSelectedStores,$filterViewModel.totalStores")
            gamesDealsViewModel.startGamesRequest("ALL",filterViewModel.currentGamePriceFilter,filterViewModel.currentGamesPageNumber.toString(),filterViewModel.sortMode)
        }else {
            Log.d("Debug","2nd if $filterViewModel.totalSelectedStores,$filterViewModel.totalStores")
            gamesDealsViewModel.startGamesRequest(filterViewModel.selectedStores,filterViewModel.currentGamePriceFilter,filterViewModel.currentGamesPageNumber.toString(),filterViewModel.sortMode)
        }
    }
    private fun clearRecyclerDataAndStartGamesRequest(){
        clearGamesRecyclerData()
        startGamesDealsRequest()
    }
    private fun setUpGamesRecycler(games: Games) {
        if(games.size <= 0){
            showViewWithAnimation(view.zeroFoundLayout)
            return
        }
        Log.d("Debug","SetUpRecyclerDataGames")
        //Data Handel
        for (game in games){
            gamesRecyclerArray.add(game)
        }
        //Adapter Setup
        gamesRecyclerAdapter = GamesRecyclerAdapter(gamesRecyclerArray)
        //RecyclerLayout Setup
        view.resultsRecyclerViewLayout.visibility = View.VISIBLE
        view.resultsRecyclerViewLayout.setHasFixedSize(true)
        view.resultsRecyclerViewLayout.layoutManager = StaggeredGridLayoutManager(itemsPerRow, StaggeredGridLayoutManager.VERTICAL)
        view.resultsRecyclerViewLayout.adapter = gamesRecyclerAdapter
        //Adapter OnClick listener
        gamesRecyclerAdapter?.onItemClick = { game ->
            if (game.steamAppID != null) {
                val intent = Intent(context, GameDetailsActivity::class.java)
                intent.putExtra("gameId", game.steamAppID)
                intent.putExtra("storeId", game.storeID)
                intent.putExtra("currentPrice", game.salePrice)
                intent.putExtra("oldPrice", game.normalPrice)
                intent.putExtra("dealId", game.dealID)
                requireContext().startActivity(intent)
            } else {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("dealId", game.dealID)
                requireContext().startActivity(intent)
            }
        }
        addingNewItemsToGamesRecycler = false
        stopShimmerLayout(view.gamesShimmerLayout)
    }
    private fun clearGamesRecyclerData(){
        gamesRecyclerArray.clear()
        view.resultsRecyclerViewLayout.adapter = null
        filterViewModel.currentGamesPageNumber = 0
    }
    //Filter
    private fun showFilterBottomSheet(){
        BottomSheetBehavior.from(view.bottomSheetLayout).state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }
    //LoadMore Data
    private fun showLoadingProgressBar(){
        val tv = TypedValue()
        if (requireActivity().theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            val param = view.resultsRecyclerViewLayout.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,0,0,actionBarHeight)
            view.resultsRecyclerViewLayout.layoutParams = param
        }
        view.loadingMoreProgressBar.visibility = View.VISIBLE
    }
    private fun hideLoadingProgressBar(){
        val param = view.resultsRecyclerViewLayout.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(0,0,0,0)
        view.resultsRecyclerViewLayout.layoutParams = param
        view.loadingMoreProgressBar.visibility = View.GONE
    }
    private fun setUpNewGamesToRecycler(games:Games){
        Log.d("Debug","SetUpNewRecyclerDataGames")
        //Data Handel
        for (game in games){
            gamesRecyclerArray.add(game)
            gamesRecyclerAdapter!!.notifyItemInserted(gamesRecyclerArray.size-1)
        }
        addingNewItemsToGamesRecycler = false
        hideLoadingProgressBar()
    }
    //Views
    private fun showViewWithAnimation(v:View){
        v.alpha = 0.0f
        v.visibility = View.VISIBLE
        v.animate().alpha(1.0f)
    }
    private fun hideViewWithAnimation(v:View){
        v.animate().alpha(0.0f).setUpdateListener {
            if (!it.isRunning){
                v.visibility = View.GONE
            }
        }
    }
    private fun startShimmerLayout(v:ShimmerFrameLayout){
        view.resultsRecyclerViewLayout.visibility = View.GONE
        v.visibility = View.VISIBLE
        v.startShimmer()
    }
    private fun stopShimmerLayout(v:ShimmerFrameLayout){
        v.visibility = View.GONE
        v.stopShimmer()
    }
    //Ads
    private fun showAds(){
        //Banner Ad
        view.gamesBannerAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d("Ads","onAdLoaded")
                view.gamesBannerAd.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.d("Ads","onAdLoadedFailed: ${p0.responseInfo}, ${p0.message}")
                hideAds()
            }
        }
        view.gamesBannerAd.loadAd(adRequest)
        //InterstitialAd
        InterstitialAd.load(requireContext(),getString(R.string.games_screen_interstitial_ad), adRequest,object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("Debug", "Full screen Ad Error: $adError")
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("Debug", "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }
    private fun reloadInterstitialAd(){
        InterstitialAd.load(requireContext(),"ca-app-pub-3940256099942544/1033173712", adRequest,object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("Debug", "Full screen Ad Error: $adError")
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("Debug", "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }
    private fun hideAds(){
        view.gamesBannerAd.visibility = View.GONE
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        filterViewModel.shouldUpdateView = true
    }

    override fun onResume() {
        super.onResume()
        showAds()
    }
    override fun onDestroyView() {
        view.gamesBannerAd.destroy()
        gamesRecyclerAdapter = null
        view.resultsRecyclerViewLayout.adapter = null
        mInterstitialAd = null
        viewModelStore.clear()
        _view = null
        _storesViewModel = null
        _gamesDealsViewModel = null
        super.onDestroyView()
    }
}
