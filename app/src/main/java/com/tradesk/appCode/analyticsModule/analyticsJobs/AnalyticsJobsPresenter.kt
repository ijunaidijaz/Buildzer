package com.tradesk.appCode.analyticsModule.analyticsJobs


import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.SearchDrugEntity
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import com.tradesk.utils.PreferenceConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AnalyticsJobsPresenter<V : IAnalyticsJobsView>
@Inject internal constructor(private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers), IAnalyticsJobsPresenter<V> {


      fun searchDrugs(drug_name: String) {
//        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().searchDrug(drug_name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onHomeResponse(it) }, { this.handleError(it,"Home") })
        )
    }


    private fun onHomeResponse(it: SearchDrugEntity) {
//        mvpView!!.hideLoading()
        when(it.success){
            true->   mvpView!!.onDrugSearched(it)
            else ->  mvpView!!.onError("Home Failed!")
        }
    }


      fun appVersion() {
          val request=
              if (mPrefs.isUserLoggedIn(PreferenceConstants.USER_LOGGED_IN)) apiHelpers.restService().appVersion(mPrefs.getKeyValue(PreferenceConstants.MEMBER_TYPE))
              else apiHelpers.restService().appVersion()
        cmp.add(
            request.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onAppVersionResp(it) }, { this.handleError(it,"Version") })
        )
    }


    private fun onAppVersionResp(it: AppVersionEntity) {
        when(it.success){
            true->   mvpView!!.onAppVersionResp(it)
            else ->  mvpView!!.onError("Home Failed!")
        }
    }



}