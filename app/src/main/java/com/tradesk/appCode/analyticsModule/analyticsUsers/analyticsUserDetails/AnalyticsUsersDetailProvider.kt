package com.tradesk.appCode.analyticsModule.analyticsUsers.analyticsUserDetails

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AnalyticsUsersDetailProvider{

    @ContributesAndroidInjector(modules = [(AnalyticsUsersDetailModule::class)])
    abstract fun bindHomesActivity(): AnalyticsUsersDetailFragment
}