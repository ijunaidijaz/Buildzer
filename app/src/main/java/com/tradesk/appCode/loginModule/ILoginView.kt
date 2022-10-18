package com.tradesk.appCode.loginModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.LoginModel


interface ILoginView: MvpView {
    fun onLogin(data: LoginModel)
    fun onerror(data: String)

}