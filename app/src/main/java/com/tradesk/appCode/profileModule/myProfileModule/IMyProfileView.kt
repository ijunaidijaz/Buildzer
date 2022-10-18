package com.tradesk.appCode.profileModule.myProfileModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.ProfileModel
import com.tradesk.data.entity.SuccessModel


interface IMyProfileView: MvpView {
    fun onProfileSuccess(it: ProfileModel)
    fun onSuccess(it: SuccessModel)
    fun onDocSuccess(it: SuccessModel)
    fun onerror(it: String)

}