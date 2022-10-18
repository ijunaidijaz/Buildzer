package com.tradesk.appCode.analyticsModule.analyticsUsers

import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.ClientsListModel
import com.tradesk.data.entity.SearchDrugEntity


interface IAnalyticsUsersView: MvpView {
    fun onDrugSearched(it: SearchDrugEntity)
    fun onAppVersionResp(it: AppVersionEntity)
    fun onSubUsersResponse(it: ClientsListModel)
}

interface IAnalyticsUsersPresenter<V: IAnalyticsUsersView> : MvpPresenter<V> {
//    fun getHome(jsonObject: JsonObject)
}