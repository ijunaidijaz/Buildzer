package com.tradesk.appCode.analyticsModule.analyticsUsers.analyticsUserDetails


import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.*
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AnalyticsUsersDetailPresenter<V : IAnalyticsUsersDetailView>
@Inject internal constructor(
    private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers),
    IAnalyticsUsersDetailPresenter<V> {


    fun userproposallist(
        user_id: String,
        status: String,
        page: String,
        limit: String,
        type: String
    ) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().userproposallist(
                user_id,
                status,
                page,
                limit,
                type
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onResponse(it) }, { this.handleError(it, "Home") })
        )
    }


    private fun onResponse(it: ParticularUserModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onResponse(it)
            else -> mvpView!!.onError("Home Failed!")
        }
    }


    fun metricjobsmain(
        type: String,
        proposal_status: String,
        /*converted_to_job: String,*/
        user_id: String
    ) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().metricjobs(
                type,
                proposal_status,
                /*converted_to_job,*/user_id
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onReveneResponse(it) }, { this.handleError(it, "Home") })
        )
    }


    private fun onReveneResponse(it: ReveneModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onRevenueResponse(it)
            else -> mvpView!!.onError("Home Failed!")
        }
    }

    fun metricjobslist(
        user_id:String,
        status:String,
        page:String,
        limit:String,
        type:String
    ) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().metricjobslist(
                user_id,
                status,
                page,
                limit, type
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onListResponse(it) }, { this.handleError(it, "Home") })
        )
    }


    private fun onListResponse(it: SignleUserJobsModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onListResponse(it)
            else -> mvpView!!.onError("Home Failed!")
        }
    }


}