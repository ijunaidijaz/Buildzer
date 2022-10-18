package com.tradesk.appCode.analyticsModule.analyticsUsers

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AnalyticsUsersProvider{

    @ContributesAndroidInjector(modules = [(AnalyticsUsersModule::class)])
    abstract fun bindHomesActivity(): AnalyticsUsersFragment
}