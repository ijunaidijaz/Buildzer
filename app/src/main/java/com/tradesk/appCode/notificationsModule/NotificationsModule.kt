package com.tradesk.appCode.notificationsModule

import dagger.Module
import dagger.Provides


@Module
class NotificationsModule {
    @Provides
    internal fun providesHomeModule(HomeDetailPresenter: NotificationsPresenter<INotificationView>): INotificationPresenter<INotificationView> =
        HomeDetailPresenter

}