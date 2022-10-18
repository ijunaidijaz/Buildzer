package com.tradesk.di.module


import android.app.Application
import android.content.Context
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.network.NetModule
import com.tradesk.data.preferences.AppPreferenceHelper
import com.tradesk.data.preferences.PreferenceHelper
import com.tradesk.di.ApplicationContext
import com.tradesk.di.PreferenceInfo
import com.tradesk.utils.*
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton


@Module
class AppModule {


    @Provides
    @Singleton
    internal fun provideContext(application: Application): Context = application

    @Provides
    @PreferenceInfo
    internal fun provideprefFileName(): String = PreferenceConstants.SharedPrefenceName


    @Provides
    @Singleton
    internal fun providePrefHelper(appPreferenceHelper: AppPreferenceHelper): PreferenceHelper =
        appPreferenceHelper

    @Provides
    @Singleton
    internal fun provideApiHelper(appApiHelper: NetModule): ApiHelper = appApiHelper


    /*@Singleton
    @Provides
    internal fun provideProgessBar()= ProgressBarHandler()
*/

    @Provides
    @Singleton
    @ApplicationContext
    fun provideImageUtil(context: Context): ImageUtility {
        return ImageUtility(context)
    }

    @Provides
    @Singleton
    @ApplicationContext
    fun providePermissionFile(context: Context): PermissionFile {
        return PermissionFile(context)
    }


    @Provides
    @Singleton
    @ApplicationContext
    fun provideAppUtils(context: Context): AppUtils {
        return AppUtils(context)
    }

    @Provides
    @Singleton
    @ApplicationContext
    fun provideDialogsUtil(context: Context): DialogsUtil {
        return DialogsUtil(context)
    }

//    @Singleton
//    @Provides
//    internal fun provideDateUtil()= DateTimeUtils()

    @Provides
    internal fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()


}