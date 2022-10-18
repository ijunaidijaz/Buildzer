package com.app.picnic360.appCode.loginModule

import com.tradesk.appCode.signupModule.ISignUpView
import com.tradesk.appCode.signupModule.ISignupPresenter
import com.tradesk.appCode.signupModule.SignupPresenter
import dagger.Module
import dagger.Provides


@Module
class SignupModule {
    @Provides
    internal fun providesLoginEmailModule(loginDetailPresenter: SignupPresenter<ISignUpView>): ISignupPresenter<ISignUpView> =
        loginDetailPresenter

}