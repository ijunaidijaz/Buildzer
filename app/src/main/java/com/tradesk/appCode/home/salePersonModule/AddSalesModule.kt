package com.tradesk.appCode.home.salePersonModule

import dagger.Module
import dagger.Provides


@Module
class AddSalesModule {
    @Provides
    internal fun providesLoginEmailModule(addSlaesPresenter: AddSalesPresenter<IAddSalesView>): ISalesPresenter<IAddSalesView> =
        addSlaesPresenter

}