package com.tradesk.appCode.jobsModule

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class JobsProvider{

    @ContributesAndroidInjector(modules = [(JobsModule::class)])
    abstract fun bindJobsActivity(): JobsFragment
}