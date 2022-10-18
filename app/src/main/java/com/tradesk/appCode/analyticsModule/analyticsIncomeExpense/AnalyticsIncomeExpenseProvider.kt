package com.tradesk.appCode.analyticsModule.analyticsIncomeExpense

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AnalyticsIncomeExpenseProvider{

    @ContributesAndroidInjector(modules = [(AnalyticsIncomeExpenseModule::class)])
    abstract fun bindHomesActivity(): AnalyticsIncomeExpenseFragment
}