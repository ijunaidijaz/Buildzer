package com.tradesk.appCode.calendarModule

import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView
import com.tradesk.data.entity.RemindersModel
import com.tradesk.data.entity.SuccessModel


interface ICalendarView: MvpView {
    fun onDrugSearched(it: RemindersModel)
    fun onAddReminder(it: SuccessModel)
}

interface ICalendarPresenter<V: ICalendarView> : MvpPresenter<V> {
//    fun getHome(jsonObject: JsonObject)
}