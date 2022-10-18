package com.tradesk.appCode.analyticsModule.analyticsHome

import dagger.Module
import dagger.Provides


@Module
class AnalyticsHomeModule {
    @Provides
    internal fun providesHomeModule(HomeDetailPresenter: AnalyticsHomePresenter<IAnalyticsHomeView>): IAnalyticsHomePresenter<IAnalyticsHomeView> =
        HomeDetailPresenter

}