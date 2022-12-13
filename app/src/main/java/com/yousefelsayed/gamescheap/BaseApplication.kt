package com.yousefelsayed.gamescheap

import com.yousefelsayed.gamescheap.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

//import com.yousefelsayed.gamescheap.modules.DaggerApplicationComponent

class BaseApplication : DaggerApplication() {

    private val applicationInjector = DaggerApplicationComponent.builder().application(this).build()
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = applicationInjector

}