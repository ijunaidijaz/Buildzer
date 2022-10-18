package com.tradesk.appCode.jobsModule

import dagger.Module
import dagger.Provides


@Module
class JobsModule {
    @Provides
    internal fun providesHomeModule(HomeDetailPresenter: JobsPresenter<IJobsView>): IJobsPresenter<IJobsView> =
        HomeDetailPresenter

}