package com.tradesk.appCode.analyticsModule.analyticsIncomeExpense

import dagger.Module
import dagger.Provides


@Module
class AnalyticsIncomeExpenseModule {
    @Provides
    internal fun providesHomeModule(HomeDetailPresenter: AnalyticsIncomeExpensePresenter<IAnalyticsIncomeExpenseView>): IAnalyticsIncomeExpensePresenter<IAnalyticsIncomeExpenseView> =
        HomeDetailPresenter

}