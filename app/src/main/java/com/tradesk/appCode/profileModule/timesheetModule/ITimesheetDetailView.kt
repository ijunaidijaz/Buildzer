package com.tradesk.appCode.profileModule.timesheetModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.*


interface ITimesheetDetailView: MvpView {
    fun onDetails(it: NewTimeSheetModelClass)
    fun onerror(data: String)

}