package com.tradesk.appCode.profileModule.proposalsModule

import dagger.Module
import dagger.Provides


@Module
class ProposalsModule {
    @Provides
    internal fun providesProposalsModule(propsoalsPresenter: PropsoalsPresenter<IProposalsView>): IProposalsPresenter<IProposalsView> =
        propsoalsPresenter

}