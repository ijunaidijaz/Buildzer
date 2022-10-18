package com.tradesk.appCode.profileModule

import com.tradesk.base.MvpPresenter
import com.tradesk.base.MvpView
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.ProfileModel


interface IProfileView: MvpView {
    fun onDrugSearched(it: ProfileModel)
    fun onAppVersionResp(it: AppVersionEntity)
//    fun onHomesResponse(it: AllHomesEntity)
}

interface IProfilePresenter<V: IProfileView> : MvpPresenter<V> {
//    fun getHome(jsonObject: JsonObject)
}