package com.tradesk.appCode.analyticsModule.analyitcsJobsModule

import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.SearchDrugEntity
import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView


interface IAnalyticsJobsDetailsView: MvpView {
    fun onDrugSearched(it: SearchDrugEntity)
    fun onAppVersionResp(it: AppVersionEntity)
//    fun onHomesResponse(it: AllHomesEntity)
}

interface IAnalyticsJobsDetailsPresenter<V: IAnalyticsJobsDetailsView> : MvpPresenter<V> {
//    fun getHome(jsonObject: JsonObject)
}