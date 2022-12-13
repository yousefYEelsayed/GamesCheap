package com.yousefelsayed.gamescheap.di.modules

import com.yousefelsayed.gamescheap.fragment.GamesFragment
import com.yousefelsayed.gamescheap.fragment.HomeFragment
import com.yousefelsayed.gamescheap.fragment.SearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun homeFragment(): HomeFragment
    @ContributesAndroidInjector
    abstract fun gamesFragment(): GamesFragment
    @ContributesAndroidInjector
    abstract fun searchFragment(): SearchFragment
}