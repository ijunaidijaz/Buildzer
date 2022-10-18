package com.tradesk.appCode.calendarModule.monthDetailModule

import dagger.Module
import dagger.Provides


@Module
class CalendarModule {
    @Provides
    internal fun providesCalendarModule(propsoalsPresenter: CalendarDetailPresenter<ICalendarDetqilView>): ICalendarDetailPresenter<ICalendarDetqilView> =
        propsoalsPresenter

}