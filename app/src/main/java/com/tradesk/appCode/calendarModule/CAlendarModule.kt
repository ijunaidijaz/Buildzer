package com.tradesk.appCode.calendarModule

import dagger.Module
import dagger.Provides


@Module
class CAlendarModule {
    @Provides
    internal fun providesHomeModule(HomeDetailPresenter: CalendarPresenter<ICalendarView>): ICalendarPresenter<ICalendarView> =
        HomeDetailPresenter

}