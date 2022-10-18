package com.app.picnic360.appCode.loginModule

import com.tradesk.appCode.profileModule.settingsModule.changePassword.ChangePasswordPresenter
import com.tradesk.appCode.profileModule.settingsModule.changePassword.IChangePasswordPresenter
import com.tradesk.appCode.profileModule.settingsModule.changePassword.IChangePasswordView
import dagger.Module
import dagger.Provides


@Module
class ChangePasswordModule {
    @Provides
    internal fun providesLoginEmailModule(loginDetailPresenter: ChangePasswordPresenter<IChangePasswordView>): IChangePasswordPresenter<IChangePasswordView> =
        loginDetailPresenter

}