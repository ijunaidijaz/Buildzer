package com.tradesk.appCode.home.leadsDetail

import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.ILeadDetailView
import dagger.Module
import dagger.Provides


@Module
class LeadDetailModule {
    @Provides
    internal fun providesLoginEmailModule(addSlaesPresenter: LeadDetailPresenter<ILeadDetailView>): ILeadDetailPresenter<ILeadDetailView> =
        addSlaesPresenter

}