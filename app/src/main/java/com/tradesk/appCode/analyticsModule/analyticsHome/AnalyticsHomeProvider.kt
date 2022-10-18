package com.tradesk.appCode.analyticsModule.analyticsHome

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AnalyticsHomeProvider{

    @ContributesAndroidInjector(modules = [(AnalyticsHomeModule::class)])
    abstract fun bindHomesActivity(): AnalyticsHomeFragment
}