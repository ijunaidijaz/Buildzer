package com.tradesk.appCode.calendarModule.monthDetailModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.*


interface ICalendarDetqilView: MvpView {
    fun onDrugSearched(it: RemindersModel)
    fun onList(it: CalendarDetailModel)
    fun onerror(data: String)
    fun onAddReminder(it: SuccessModel)

}