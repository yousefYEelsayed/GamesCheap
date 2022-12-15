package com.yousefelsayed.gamescheap.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.activity.WebViewActivity
import com.yousefelsayed.gamescheap.adapter.SearchResultsRecyclerAdapter
import com.yousefelsayed.gamescheap.api.Status
import com.yousefelsayed.gamescheap.databinding.FragmentSearchBinding
import com.yousefelsayed.gamescheap.model.SearchModel
import com.yousefelsayed.gamescheap.model.SearchModelItem
import com.yousefelsayed.gamescheap.viewmodel.SearchFragmentViewModel
import com.yousefelsayed.gamescheap.viewmodel.SearchFragmentViewModelFactory
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchFragment: DaggerFragment() {

    //Design
    private var _view:FragmentSearchBinding? = null
    private val view get() = _view!!
    //Backend
    @Inject
    lateinit var searchFragmentViewModelFactory: SearchFragmentViewModelFactory
    private lateinit var searchFragmentViewModel: SearchFragmentViewModel
    //Screen Orientation
    private var itemsPerRow = 2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _view = DataBindingUtil.inflate(inflater, R.layout.fragment_search,container,false)
        init()
        checkScreenOrientation()
        setupListeners()
        setupObservers()
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
        searchFragmentViewModel = ViewModelProvider(this@SearchFragment,searchFragmentViewModelFactory)[SearchFragmentViewModel::class.java]
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners(){
        view.searchEditText.setOnTouchListener { _, p1 ->
            if (p1?.action?.equals(MotionEvent.ACTION_DOWN) == true) {
                view.constraintLayout.transitionToEnd()
            }
            false
        }
        view.searchEditText.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                if(view.constraintLayout.currentState != R.id.end){
                    view.constraintLayout.transitionToState(R.id.end,1)
                }
            }

            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable) {
                startSearchForResults(p0.toString())
            }

        })
        view.serverErrorLayoutInclude.serverErrorRetryButton.setOnClickListener{
            view.serverErrorLayoutInclude.serverErrorMainLayout.setVisibilityForMotionLayout(View.GONE)
            startSearchForResults(view.searchEditText.text.toString())
        }
    }
    private fun setupObservers(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                searchFragmentViewModel.searchData.collect{ result ->
                    clearRecyclerView()
                    when(result.status){
                        Status.LOADING -> view.loadingRelativeLayout.startShimmer()
                        Status.SUCCESS -> setUpSearchResultsRecyclerData(result.data!!)
                        Status.ERROR -> view.serverErrorLayoutInclude.serverErrorMainLayout.setVisibilityForMotionLayout(View.VISIBLE)
                    }
                }
            }
        }
    }
    private fun checkScreenOrientation(){
        itemsPerRow = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2
        else 4
        view.shimmerGridLayout.columnCount = itemsPerRow
    }

    private fun startSearchForResults(cleanSearchString:String){
        if (!checkForInternet()) return
        view.searchRecyclerView.setVisibilityForMotionLayout(View.INVISIBLE)
        view.searchZeroFoundLayout.setVisibilityForMotionLayout(View.INVISIBLE)
        view.loadingRelativeLayout.setVisibilityForMotionLayout(View.VISIBLE)
        view.loadingRelativeLayout.startShimmer()
        searchFragmentViewModel.startSearch(cleanSearchString)

    }
    private fun setUpSearchResultsRecyclerData(it: SearchModel) {
        //Check results are found
        if (it.size <= 0){
            view.loadingRelativeLayout.stopShimmer()
            view.loadingRelativeLayout.setVisibilityForMotionLayout(View.GONE)
            view.searchRecyclerView.setVisibilityForMotionLayout(View.INVISIBLE)
            view.searchZeroFoundLayout.setVisibilityForMotionLayout(View.VISIBLE)
            return
        }
        //Data handel
        val startData = SearchModelItem("test","falseData","Found ${it.size} Results!","666","test","test","test")
        if (it.size > 0 && it[0].gameID != "666" && !it.contains(startData)){
            it.add(0,startData)
        }
        val data = ArrayList<SearchModelItem>(it)
        //Setup RecyclerView
        view.loadingRelativeLayout.stopShimmer()
        view.loadingRelativeLayout.setVisibilityForMotionLayout(View.GONE)
        view.searchRecyclerView.setVisibilityForMotionLayout(View.VISIBLE)
        view.searchRecyclerView.setHasFixedSize(true)
        view.searchRecyclerView.setItemViewCacheSize(25)
        view.searchRecyclerView.layoutManager = StaggeredGridLayoutManager(itemsPerRow, StaggeredGridLayoutManager.VERTICAL)
        val adapter = SearchResultsRecyclerAdapter(data)
        view.searchRecyclerView.adapter = adapter
        adapter.onItemClick = {
            if (it.cheapestDealID != "falseData"){
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("dealId", it.cheapestDealID)
                requireContext().startActivity(intent)
            }
        }
    }
    private fun clearRecyclerView(){
        view.searchRecyclerView.layoutManager = null
        view.searchRecyclerView.adapter = null
    }
    //Custom visibility update because the normal visibility changer doesn't work with MotionLayout
    private fun View.setVisibilityForMotionLayout(visibility: Int) {
        val motionLayout = parent as MotionLayout
        motionLayout.constraintSetIds.forEach {
            val constraintSet = motionLayout.getConstraintSet(it) ?: return@forEach
            constraintSet.setVisibility(this.id, visibility)
            constraintSet.applyTo(motionLayout)
        }
    }

    override fun onDestroyView() {
        _view = null
        super.onDestroyView()
    }
}