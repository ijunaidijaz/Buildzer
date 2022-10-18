package com.tradesk.appCode.analyticsModule.analyticsTimesheet

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TimesheetProvider{

    @ContributesAndroidInjector(modules = [(TimesheetModule::class)])
    abstract fun bindJObsActivity(): AnalyticsTimeSheetFragment
}