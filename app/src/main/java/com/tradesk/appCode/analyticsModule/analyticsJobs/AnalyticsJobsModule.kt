package com.tradesk.appCode.analyticsModule.analyticsJobs

import dagger.Module
import dagger.Provides


@Module
class AnalyticsJobsModule {
    @Provides
    internal fun providesHomeModule(HomeDetailPresenter: AnalyticsJobsPresenter<IAnalyticsJobsView>): IAnalyticsJobsPresenter<IAnalyticsJobsView> =
        HomeDetailPresenter

}