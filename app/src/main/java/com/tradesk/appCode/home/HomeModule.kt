package com.tradesk.appCode.home

import dagger.Module
import dagger.Provides


@Module
class HomeModule {
    @Provides
    internal fun providesHomeModule(HomeDetailPresenter: HomePresenter<IHomeView>): IHomePresenter<IHomeView> =
        HomeDetailPresenter

}