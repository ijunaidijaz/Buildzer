package com.tradesk.appCode.analyticsModule.analyticsTimesheet


import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.*
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TimesheetPresenter<V : ITimeSheetView>
@Inject internal constructor(private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers), ITimeSheetPresenter<V> {


    fun timesheetlist(page: String,limit:String,user_id:String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().timesheetlist(page,limit,user_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onJobsResponse(it) }, { this.handleError(it,"Home") })
        )
    }


    private fun onJobsResponse(it: TimeModelNewUPdate) {
        mvpView!!.hideLoading()
        when(it.status){
            200->   mvpView!!.onDetail(it)
            else ->  mvpView!!.onError("Home Failed!")
        }
    }

    fun gettime(job_id: String,status:String,start_date:String,enddate:String,timezone:String,address:String,city: String,state:String,zipcode: String,latLong:String) {
//        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().gettime(job_id,status,start_date,enddate,timezone,address,city,state,zipcode,latLong)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onGetResponse(it) }, { this.handleError(it,"Home") })
        )
    }


    private fun onGetResponse(it: ClockInOutModel) {
//        mvpView!!.hideLoading()
        when(it.status){
            200->   mvpView!!.onGetResponse(it)
            else ->  mvpView!!.onError("Home Failed!")
        }
    }

    fun addtime(job_id: String,status:String,end_date: String,timezone: String,address:String,city: String,state:String,zipcode: String,latLong:String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().intime(job_id,status,end_date,timezone,address,city,state,zipcode,latLong)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onAddResponse(it) }, { this.handleError(it,"Home") })
        )
    }


    private fun onAddResponse(it: ClockInOutModel) {
        mvpView!!.hideLoading()
        when(it.status){
            200->   mvpView!!.onInResponse(it)
            else ->  mvpView!!.onError("Home Failed!")
        }
    }

    fun outTime(job_id: String,status:String,start_date: String,end_date: String,timezone:String,address:String,city: String,state:String,zipcode: String,latLong:String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().outtime(job_id,status,start_date,end_date,timezone,address,city,state,zipcode,latLong)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onOutResponse(it) }, { this.handleError(it,"Home") })
        )
    }


    private fun onOutResponse(it: ClockInOutModel) {
        mvpView!!.hideLoading()
        when(it.status){
            200->   mvpView!!.onOutResponse(it)
            else ->  mvpView!!.onError("Home Failed!")
        }
    }

    fun userslist(page: String, limit: String, trade: String) {
//        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().saleslist("sales",page, limit, trade)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onResponse(it) }, { this.handleError(it, "Home") })
        )
    }


    private fun onResponse(it: ClientsListModel) {
//        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onSubUsersResponse(it)
            else -> mvpView!!.onError("Home Failed!")
        }
    }





}