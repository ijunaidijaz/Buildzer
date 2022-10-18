package com.tradesk.appCode.home

import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.LeadsModel
import com.tradesk.data.entity.ProfileModel


interface IHomeView : MvpView {
    fun onLeads(it: LeadsModel)
    fun onDrugSearched(it: ProfileModel)
    fun onAppVersionResp(it: AppVersionEntity)
}

interface IHomePresenter<V : IHomeView> : MvpPresenter<V> {
//    fun getHome(jsonObject: JsonObject)
}