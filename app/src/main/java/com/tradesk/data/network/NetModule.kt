package com.tradesk.data.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.tradesk.BuildConfig
import com.tradesk.data.preferences.PreferenceHelper
import com.tradesk.utils.PreferenceConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class NetModule
@Inject constructor(
    internal var context: Context,
    internal val preferenceHelper: PreferenceHelper
) :
    ApiHelper {
    override fun distanceService(): RestService {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofitSerice = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(unsafeOkHttpClient2)
            .build()
        return retrofitSerice.create(RestService::class.java)
    }


    override fun authdetailService(): RestService {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofitSerice = Retrofit.Builder()
            .baseUrl("NetworkConstants.AuthURL")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(unsafeOkHttpClient2)
            .build()
        return retrofitSerice.create(RestService::class.java)
    }

    override fun restService(): RestService {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(unsafeOkHttpClient)
            .build()
        return retrofit.create(RestService::class.java)
    }

    val unsafeOkHttpClient: OkHttpClient
        get() {
            try {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                val httpClient = OkHttpClient.Builder()
                httpClient.interceptors().add(getHeadersForApis())
                httpClient.interceptors().add(httpLoggingInterceptor)
                httpClient.readTimeout(30, TimeUnit.SECONDS)
                httpClient.connectTimeout(30, TimeUnit.SECONDS)
                return httpClient.build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }
    val unsafeOkHttpClient2: OkHttpClient
        get() {
            try {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                val httpClient = OkHttpClient.Builder()
                httpClient.interceptors().add(httpLoggingInterceptor)
                httpClient.interceptors().add(getHeadersForApis())
                httpClient.readTimeout(30, TimeUnit.SECONDS)
                httpClient.connectTimeout(30, TimeUnit.SECONDS)
                return httpClient.build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

    private fun getHeadersForApis(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (preferenceHelper.isUserLoggedIn(PreferenceConstants.USER_LOGGED_IN)) {
                request = request.newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer " + preferenceHelper.getKeyValue(PreferenceConstants.USER_TOKEN)
                    )
                    .build()
            }
            chain.proceed(request)
        }
    }
}
