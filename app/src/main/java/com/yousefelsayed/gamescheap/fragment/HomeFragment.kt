package com.yousefelsayed.gamescheap.fragment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.*
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.activity.GameDetailsActivity
import com.yousefelsayed.gamescheap.activity.WebViewActivity
import com.yousefelsayed.gamescheap.adapter.SteamEpicGamesRecyclerAdapter
import com.yousefelsayed.gamescheap.adapter.TopGamesRecyclerAdapter
import com.yousefelsayed.gamescheap.databinding.FragmentHomeBinding
import com.yousefelsayed.gamescheap.model.Games
import com.yousefelsayed.gamescheap.model.GamesItem
import com.yousefelsayed.gamescheap.viewmodel.GamesViewModel
import com.yousefelsayed.gamescheap.viewmodel.GamesViewModelFactory
import com.yousefelsayed.gamescheap.viewmodel.SteamAndEpicGamesViewModel
import com.yousefelsayed.gamescheap.viewmodel.SteamAndEpicGamesViewModelFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import kotlin.collections.ArrayList

class HomeFragment : DaggerFragment() {

    private var _view:FragmentHomeBinding? = null
    private val view get() = _view!!
    //Steam and EpicGames backend
    @Inject
    lateinit var steamAndEpicGamesViewModelFactory: SteamAndEpicGamesViewModelFactory
    private var _steamAndEpicGamesViewModel: SteamAndEpicGamesViewModel? = null
    private val steamAndEpicGamesViewModel: SteamAndEpicGamesViewModel get() = _steamAndEpicGamesViewModel!!
    private var steamEpicGamesAdapter:SteamEpicGamesRecyclerAdapter? = null
    //Top Games Recyclerview backend
    @Inject
    lateinit var gamesViewModelFactory: GamesViewModelFactory
    private var _gamesViewModel: GamesViewModel? = null
    private val gamesViewModel: GamesViewModel get() = _gamesViewModel!!
    private var topGamesAdapter:TopGamesRecyclerAdapter? = null
    //Ads
    private val adRequest = AdRequest.Builder().build()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _view = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        retainInstance = true
        init()
        setUpListeners()
        setUpObservers()
        startSteamEpicGamesApiCall()
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
    //Views functions
    private fun init(){
        //Backend
        _steamAndEpicGamesViewModel = ViewModelProvider(this@HomeFragment, steamAndEpicGamesViewModelFactory)[SteamAndEpicGamesViewModel::class.java]
        _gamesViewModel = ViewModelProvider(this@HomeFragment, gamesViewModelFactory)[GamesViewModel::class.java]
    }
    private fun setUpListeners(){
        view.viewMoreButton.setOnClickListener{
            findNavController().navigate(R.id.gamesFragment)
        }
        view.serverErrorLayoutInclude.serverErrorRetryButton.setOnClickListener {
            startSteamEpicGamesApiCall()
            hideViewWithAnimation(view.serverErrorLayoutInclude.serverErrorMainLayout)
        }
    }
    private fun setUpObservers(){
        steamAndEpicGamesViewModel.requestStatus.observe(viewLifecycleOwner) {
            if (it == "SUCCESS"){
                setUpSteamEpicGamesRecycler(steamAndEpicGamesViewModel.games.value!!)
            }else if(it != ""){
                showViewWithAnimation(view.serverErrorLayoutInclude.serverErrorMainLayout)
            }
        }
        gamesViewModel.requestStatus.observe(viewLifecycleOwner) {
            if (it == "SUCCESS"){
                setUpTopGamesRecycler(gamesViewModel.games.value!!)
            }else if(it != ""){
                showViewWithAnimation(view.serverErrorLayoutInclude.serverErrorMainLayout)
            }
        }
    }
    //Steam-EpicGames functions
    private fun startSteamEpicGamesApiCall(){
        if (!checkForInternet()){
            return
        }
        steamAndEpicGamesViewModel.startSteamEpicGamesRequest()
    }
    private fun setUpSteamEpicGamesRecycler(games: Games){
        view.sectionsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)
        val steamEpicGamesData = ArrayList<GamesItem>(games)
        steamEpicGamesAdapter = SteamEpicGamesRecyclerAdapter(steamEpicGamesData)
        //Adapter onCLickListener
        steamEpicGamesAdapter?.onItemClick = { game ->
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
        //Hide progressBar and show data
        hideShimmerView(view.steamEpicGamesShimmerLayout)
        view.sectionsRecyclerView.adapter = steamEpicGamesAdapter
        startTopGamesApiCall()
    }
    //Top Games functions
    private fun startTopGamesApiCall(){
        if (!checkForInternet()){
            return
        }
        gamesViewModel.startGamesRequest()
    }
    private fun setUpTopGamesRecycler(games: Games) {
        view.topGamesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL ,false)
        val mainData = ArrayList<GamesItem>(games)
        val filteredData = mainData.distinctBy { it.gameID }
        topGamesAdapter = TopGamesRecyclerAdapter(filteredData)
        //Adapter onCLickListener
        topGamesAdapter?.onItemClick = { game ->
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
        //Hide progressBar and show data
        view.viewMoreButton.visibility = View.VISIBLE
        view.emptySpace.visibility = View.VISIBLE
        hideShimmerView(view.topGamesShimmerView)
        view.topGamesRecyclerView.adapter = topGamesAdapter
        //Adding bottomPadding to make all items usable
        val tv = TypedValue()
        if (requireActivity().theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics) + 30
            val params = view.emptySpace.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0,0,0,actionBarHeight)
            view.emptySpace.layoutParams = params
        }
        showAds()
    }
    //Views Animation
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
    private fun hideShimmerView(v:ShimmerFrameLayout){
        v.visibility = View.INVISIBLE
        v.stopShimmer()
    }
    //Ads
    private fun showAds(){
        view.homeScreenBannerAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                view.homeScreenBannerAd.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.d("Ads","onAdLoadedFailed: ${p0.responseInfo}, ${p0.message}")
                hideAds()
            }
        }
        view.homeScreenBannerAd.loadAd(adRequest)
    }
    private fun hideAds(){
        view.homeScreenBannerAd.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        //Views
        view.steamEpicGamesShimmerLayout.startShimmer()
        view.topGamesShimmerView.startShimmer()
    }

    override fun onDestroyView() {
        view.homeScreenBannerAd.destroy()
        steamEpicGamesAdapter = null
        topGamesAdapter = null
        view.topGamesRecyclerView.adapter = null
        view.sectionsRecyclerView.adapter = null
        _view = null
        _steamAndEpicGamesViewModel = null
        _gamesViewModel = null
        viewModelStore.clear()
        super.onDestroyView()
    }
}