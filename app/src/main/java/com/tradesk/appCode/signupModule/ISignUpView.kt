package com.tradesk.appCode.signupModule

import com.tradesk.base.MvpView
import com.tradesk.data.entity.LoginModel
import com.tradesk.data.entity.SignupModel
import com.tradesk.data.entity.SuccessModel


interface ISignUpView: MvpView {
    fun onSignup(data: SignupModel)
    fun onSocialSignup(data: LoginModel)
    fun onForgot(data: SuccessModel)
    fun onerror(data: String)

}