package com.tradesk.appCode.home.addLeadsModule

import dagger.Module
import dagger.Provides


@Module
class AddLeadsModule {
    @Provides
    internal fun providesLoginEmailModule(addSlaesPresenter: AddLeadsPresenter<IAddLeadsView>): IAddLeadsPresenter<IAddLeadsView> =
        addSlaesPresenter

}