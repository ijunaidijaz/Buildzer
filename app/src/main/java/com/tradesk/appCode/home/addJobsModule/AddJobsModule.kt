package com.tradesk.appCode.home.addJobsModule

import dagger.Module
import dagger.Provides


@Module
class AddJobsModule {
    @Provides
    internal fun providesLoginEmailModule(addSlaesPresenter: AddJobsPresenter<IAddJobView>): IAddJobPresenter<IAddJobView> =
        addSlaesPresenter

}