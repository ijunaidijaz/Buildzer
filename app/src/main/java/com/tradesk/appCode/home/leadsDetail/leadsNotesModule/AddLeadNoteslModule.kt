package com.tradesk.appCode.home.leadsDetail.leadsNotesModule

import dagger.Module
import dagger.Provides


@Module
class AddLeadNoteslModule {
    @Provides
    internal fun providesLoginEmailModule(addSlaesPresenter: LeadsNotesPresenter<ILeadNotesView>): ILeadsNotesPresenter<ILeadNotesView> =
        addSlaesPresenter

}