package com.app.picnic360.appCode.loginModule

import com.tradesk.appCode.loginModule.ILoginPresenter
import com.tradesk.appCode.loginModule.ILoginView
import com.tradesk.appCode.loginModule.LoginPresenter
import dagger.Module
import dagger.Provides


@Module
class LoginModule {
    @Provides
    internal fun providesLoginEmailModule(loginDetailPresenter: LoginPresenter<ILoginView>): ILoginPresenter<ILoginView> =
        loginDetailPresenter

}