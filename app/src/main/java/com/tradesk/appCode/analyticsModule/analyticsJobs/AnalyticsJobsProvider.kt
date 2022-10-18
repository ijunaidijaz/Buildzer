package com.tradesk.appCode.analyticsModule.analyticsJobs

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AnalyticsJobsProvider{

    @ContributesAndroidInjector(modules = [(AnalyticsJobsModule::class)])
    abstract fun bindJObsActivity(): AnalyticsJobsFragment
}