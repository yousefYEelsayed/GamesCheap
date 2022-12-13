package com.yousefelsayed.gamescheap.di

import android.app.Application
import com.yousefelsayed.gamescheap.BaseApplication
import com.yousefelsayed.gamescheap.di.modules.FragmentBuildersModule
import com.yousefelsayed.gamescheap.di.modules.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, FragmentBuildersModule::class, NetworkModule::class])
interface ApplicationComponent : AndroidInjector<BaseApplication>{

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): ApplicationComponent
    }

    override fun inject(app: BaseApplication)

}