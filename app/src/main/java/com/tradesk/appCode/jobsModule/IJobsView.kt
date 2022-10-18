package com.tradesk.appCode.jobsModule

import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.LeadsModel


interface IJobsView : MvpView {
    fun onJobsList(it: LeadsModel)
    fun onAppVersionResp(it: AppVersionEntity)
}

interface IJobsPresenter<V : IJobsView> : MvpPresenter<V> {
//    fun getHome(jsonObject: JsonObject)
}