package com.tradesk.appCode.analyticsModule.analyticsTimesheet

import dagger.Module
import dagger.Provides


@Module
class TimesheetModule {
    @Provides
    internal fun providesHomeModule(HomeDetailPresenter: TimesheetPresenter<ITimeSheetView>): ITimeSheetPresenter<ITimeSheetView> =
        HomeDetailPresenter

}