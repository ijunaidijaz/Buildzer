package com.tradesk.appCode.analyticsModule.analyticsTimesheet

import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView
import com.tradesk.data.entity.*


interface ITimeSheetView: MvpView {
    fun onDetail(it: TimeModelNewUPdate)
    fun onGetResponse(it: ClockInOutModel)
    fun onInResponse(it: ClockInOutModel)
    fun onOutResponse(it: ClockInOutModel)
    fun onSubUsersResponse(it: ClientsListModel)
}

interface ITimeSheetPresenter<V: ITimeSheetView> : MvpPresenter<V> {
//    fun getHome(jsonObject: JsonObject)
}