package com.tradesk.appCode.profileModule.timesheetModule.jobTimesheetModule

import dagger.Module
import dagger.Provides


@Module
class JobTimesheetModule {
    @Provides
    internal fun providesJobsTimesheetModule(jobTimesheetPresenter: JobTimesheetPresenter<IJobTimesheetView>): IJobTimesheetPresenter<IJobTimesheetView> =
        jobTimesheetPresenter

}