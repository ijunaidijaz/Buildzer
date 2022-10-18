package com.tradesk.appCode.analyticsModule.analyticsUsers

import dagger.Module
import dagger.Provides


@Module
class AnalyticsUsersModule {
    @Provides
    internal fun providesHomeModule(HomeDetailPresenter: AnalyticsUsersPresenter<IAnalyticsUsersView>): IAnalyticsUsersPresenter<IAnalyticsUsersView> =
        HomeDetailPresenter

}