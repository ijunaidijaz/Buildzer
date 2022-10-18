package com.tradesk.appCode.addExpensesModule

import dagger.Module
import dagger.Provides


@Module
class ExpensesModule {
    @Provides
    internal fun providesProposalsModule(propsoalsPresenter: ExpensesPresenter<IExpensesView>): IExpensesPresenter<IExpensesView> =
        propsoalsPresenter

}