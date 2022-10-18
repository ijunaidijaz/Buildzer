package com.app.picnic360.appCode.loginModule

import com.tradesk.appCode.profileModule.myProfileModule.IMyProfileView
import com.tradesk.appCode.profileModule.myProfileModule.IProfilePresenter
import com.tradesk.appCode.profileModule.myProfileModule.ProfilePresenter
import dagger.Module
import dagger.Provides


@Module
class ProfileModule {
    @Provides
    internal fun providesLoginEmailModule(loginDetailPresenter: ProfilePresenter<IMyProfileView>): IProfilePresenter<IMyProfileView> =
        loginDetailPresenter

}