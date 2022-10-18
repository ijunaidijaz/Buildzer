package com.tradesk.appCode.analyticsModule.analyitcsJobsModule

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AnalyticsJobsDetailsProvider{

    @ContributesAndroidInjector(modules = [(AnalyticsJobsDetailsModule::class)])
    abstract fun bindJObsActivity(): AnalyticsJobsDetailsFragment
}