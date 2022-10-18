package com.tradesk.appCode.notificationsModule

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NotificationProvider{

    @ContributesAndroidInjector(modules = [(NotificationsModule::class)])
    abstract fun bindHomesActivity(): NotificationFragment
}