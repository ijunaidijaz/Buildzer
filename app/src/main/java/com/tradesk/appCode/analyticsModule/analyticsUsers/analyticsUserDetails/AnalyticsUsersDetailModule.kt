package com.tradesk.appCode.analyticsModule.analyticsUsers.analyticsUserDetails

import dagger.Module
import dagger.Provides


@Module
class AnalyticsUsersDetailModule {
    @Provides
    internal fun providesHomeModule(HomeDetailPresenter: AnalyticsUsersDetailPresenter<IAnalyticsUsersDetailView>): IAnalyticsUsersDetailPresenter<IAnalyticsUsersDetailView> =
        HomeDetailPresenter

}