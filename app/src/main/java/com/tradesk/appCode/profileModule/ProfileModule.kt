package com.tradesk.appCode.profileModule

import dagger.Module
import dagger.Provides


@Module
class ProfileModule {
    @Provides
    internal fun providesHomeModule(HomeDetailPresenter: ProfilePresenter<IProfileView>): IProfilePresenter<IProfileView> =
        HomeDetailPresenter

}