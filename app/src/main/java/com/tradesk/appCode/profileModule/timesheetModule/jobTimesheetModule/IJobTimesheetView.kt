package com.tradesk.appCode.profileModule.timesheetModule.jobTimesheetModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.*


interface IJobTimesheetView: MvpView {

    fun onDetail(it: TimeModelNewUPdate)
    fun onGetResponse(it: ClockInOutModel)
    fun onInResponse(it: ClockInOutModel)
    fun onOutResponse(it: ClockInOutModel)

    fun onDetails(it: NewTimeSheetModelClass)
    fun onerror(data: String)

}