package com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule

import dagger.Module
import dagger.Provides


@Module
class ChatModule {
    @Provides
    internal fun providesChatModule(presenter: ChatPresenter<IChatView>): IChatPresenter<IChatView> =
        presenter

}