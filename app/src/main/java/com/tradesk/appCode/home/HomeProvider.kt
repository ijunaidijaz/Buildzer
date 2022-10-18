package com.tradesk.appCode.home

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeProvider {

    @ContributesAndroidInjector(modules = [(HomeModule::class)])
    abstract fun bindHomesActivity(): HomeFragment
}