package com.tradesk.appCode.profileModule.timesheetModule

import dagger.Module
import dagger.Provides


@Module
class TimesheetDetailModule {
    @Provides
    internal fun providesProposalsModule(propsoalsPresenter: TimesheetDetailPresenter<ITimesheetDetailView>): ITimesheetDetailPresenter<ITimesheetDetailView> =
        propsoalsPresenter

}