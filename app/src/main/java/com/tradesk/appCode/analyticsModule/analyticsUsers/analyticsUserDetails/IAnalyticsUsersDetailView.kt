package com.tradesk.appCode.analyticsModule.analyticsUsers.analyticsUserDetails

import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView
import com.tradesk.data.entity.*


interface IAnalyticsUsersDetailView: MvpView {
    fun onResponse(it: ParticularUserModel)
    fun onRevenueResponse(it: ReveneModel)
    fun onListResponse(it: SignleUserJobsModel)
}

interface IAnalyticsUsersDetailPresenter<V: IAnalyticsUsersDetailView> : MvpPresenter<V> {
}