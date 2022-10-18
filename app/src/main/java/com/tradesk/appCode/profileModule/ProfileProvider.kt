package com.tradesk.appCode.profileModule

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ProfileProvider{

    @ContributesAndroidInjector(modules = [(ProfileModule::class)])
    abstract fun bindHomesActivity(): ProfileFragment
}