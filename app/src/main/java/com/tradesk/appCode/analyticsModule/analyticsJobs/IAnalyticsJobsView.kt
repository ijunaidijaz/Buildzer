package com.tradesk.appCode.analyticsModule.analyticsJobs

import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.SearchDrugEntity


interface IAnalyticsJobsView: MvpView {
    fun onDrugSearched(it: SearchDrugEntity)
    fun onAppVersionResp(it: AppVersionEntity)
//    fun onHomesResponse(it: AllHomesEntity)
}

interface IAnalyticsJobsPresenter<V: IAnalyticsJobsView> : MvpPresenter<V> {
//    fun getHome(jsonObject: JsonObject)
}