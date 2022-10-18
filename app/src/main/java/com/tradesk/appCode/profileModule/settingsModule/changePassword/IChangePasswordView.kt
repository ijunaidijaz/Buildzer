package com.tradesk.appCode.profileModule.settingsModule.changePassword

import com.tradesk.base.MvpView
import com.tradesk.data.entity.SuccessModel
import com.tradesk.data.entity.TermsModel


interface IChangePasswordView: MvpView {
    fun onSuccess(it: SuccessModel)
    fun onTermsSuccess(it: TermsModel)
    fun onerror(it: String)

}