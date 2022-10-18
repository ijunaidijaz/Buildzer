package com.tradesk.appCode.calendarModule

import com.tradesk.appCode.home.HomeModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CalendarProvider{

    @ContributesAndroidInjector(modules = [(HomeModule::class)])
    abstract fun bindHomesActivity(): CalendarFragment
}