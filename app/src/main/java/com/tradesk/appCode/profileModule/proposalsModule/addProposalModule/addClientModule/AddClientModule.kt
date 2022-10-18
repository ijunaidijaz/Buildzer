package com.app.picnic360.appCode.loginModule

import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.addClientModule.AddClientPresenter
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.addClientModule.IAddClientPresenter
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.addClientModule.IAddClientView
import dagger.Module
import dagger.Provides


@Module
class AddClientModule {
    @Provides
    internal fun providesLoginEmailModule(addClientPresenter: AddClientPresenter<IAddClientView>): IAddClientPresenter<IAddClientView> =
        addClientPresenter

}