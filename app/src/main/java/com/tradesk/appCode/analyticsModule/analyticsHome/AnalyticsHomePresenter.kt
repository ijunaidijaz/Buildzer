package com.tradesk.appCode.analyticsModule.analyticsHome


import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.*
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AnalyticsHomePresenter<V : IAnalyticsHomeView>
@Inject internal constructor(private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers), IAnalyticsHomePresenter<V> {

    fun getjobs(page: String,limit:String,status:String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getleads("job",page,limit,status)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onJobsResponse(it) }, { this.handleError(it,"Home") })
        )
    }


    private fun onJobsResponse(it: LeadsModel) {
        mvpView!!.hideLoading()
        when(it.status){
            200->   mvpView!!.onJobsList(it)
            else ->  mvpView!!.onError("Home Failed!")
        }
    }



    fun metricjobsmain(type: String,status:String) {
//        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().metricjobsmain(type,status)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onJobsMetricResponse(it) }, { this.handleError(it,"Home") })
        )
    }


    private fun onJobsMetricResponse(it: MetircJobDataModel) {
//        mvpView!!.hideLoading()
        when(it.status){
            200->   mvpView!!.onJobMetricResponse(it)
            else ->  mvpView!!.onError("Home Failed!")
        }
    }



    fun getLeads(page: String,limit:String,status:String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getleads("lead",page,limit,status)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onLeadsResponse(it) }, { this.handleError(it,"Home") })
        )
    }


    private fun onLeadsResponse(it: LeadsModel) {
        mvpView!!.hideLoading()
        when(it.status){
            200->   mvpView!!.onLeads(it)
            else ->  mvpView!!.onError("Home Failed!")
        }
    }




     fun getProposalRevenue(type: String,status: String) {
//        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getProposalRevenue(type,status)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onProposalRevResponse(it) }, { this.handleError(it,"Home") })
        )
    }


    private fun onProposalRevResponse(it: RevenueModel) {
//        mvpView!!.hideLoading()
        when(it.status){
            200->   mvpView!!.onRevenueModel(it)
            else ->  mvpView!!.onError("Home Failed!")
        }
    }


    fun getProposals(page: String,limit:String,status:String,type:String,id:String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getproposals(page,limit,status,type,id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onResponse(it: PorposalsListModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onProposals(it)
            else -> mvpView!!.onError(it.message)
        }
    }


    fun deleteproposal(id:String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().deleteproposal(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onDeleteResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onDeleteResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onDelete(it)
            else -> mvpView!!.onError(it.message)
        }
    }





    /*new api for sales added here*/

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
        converted:String,
        type:String
    ) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().metricjobslistLead(
                user_id,
                status,
                page,
                limit,converted, type
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