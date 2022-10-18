package com.tradesk.appCode.analyticsModule.analyticsIncomeExpense


import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.ExpensesJobsListModel
import com.tradesk.data.entity.PorposalsListModel
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AnalyticsIncomeExpensePresenter<V : IAnalyticsIncomeExpenseView>
@Inject internal constructor(private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers), IAnalyticsIncomeExpensePresenter<V> {


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

    private fun onResponse(it: PorposalsListModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onInvoicesResponse(it)
            else -> mvpView!!.onError(it.message)
        }
    }


    fun getjobexpenseslist(page: String,limit:String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().jobexpenseslist(page,limit )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onJobsResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    private fun onJobsResponse(it: ExpensesJobsListModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onExpenseListResponse(it)
            else -> mvpView!!.onError(it.message)
        }
    }



}