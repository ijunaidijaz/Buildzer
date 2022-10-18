package com.tradesk.appCode.analyticsModule.analyticsHome

import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView
import com.tradesk.data.entity.*


interface IAnalyticsHomeView: MvpView {
    fun onLeads(it: LeadsModel)
    fun onJobsList(it: LeadsModel)
    fun onRevenueModel(it: RevenueModel)
    fun onJobMetricResponse(it: MetircJobDataModel)
    fun onProposals(it: PorposalsListModel)
    fun onDelete(it: SuccessModel)


    fun onRevenueResponse(it: ReveneModel)
    fun onListResponse(it: SignleUserJobsModel)
}

interface IAnalyticsHomePresenter<V: IAnalyticsHomeView> : MvpPresenter<V> {
//    fun getHome(jsonObject: JsonObject)
}