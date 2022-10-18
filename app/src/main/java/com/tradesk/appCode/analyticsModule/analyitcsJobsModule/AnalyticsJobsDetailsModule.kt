package com.tradesk.appCode.analyticsModule.analyitcsJobsModule

import dagger.Module
import dagger.Provides


@Module
class AnalyticsJobsDetailsModule {
    @Provides
    internal fun providesHomeModule(HomeDetailPresenter: AnalyticsJobsDetailsPresenter<IAnalyticsJobsDetailsView>): IAnalyticsJobsDetailsPresenter<IAnalyticsJobsDetailsView> =
        HomeDetailPresenter

}