package com.tradesk.appCode.profileModule.timesheetModule.jobTimesheetModule


import android.util.Log
import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.*
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject


class JobTimesheetPresenter<V : IJobTimesheetView>
@Inject internal constructor(
    private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers),
    IJobTimesheetPresenter<V> {

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


    fun jobsdetailtimesheet(id:String) {
//        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().jobsdetailtimesheet( id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    private fun onResponse(it: NewTimeSheetModelClass) {
//        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onDetails(it)
            else -> mvpView!!.onError(it.message)
        }
    }

    override fun handleError(e: Throwable, lastAction: String) {
        e.printStackTrace()
        try {
            mvpView!!.hideLoading()

            if (e is HttpException) {
                val responseBody = e.response()!!.errorBody()
                if (e.code() == 401 || e.localizedMessage!!.contains("401 Unauthorized", false)) {
                    mvpView!!.showErrorMessage(getErrorMessage(responseBody))
                } else if (e.code() == 400) {

                    val errorStr = responseBody!!.string()
                    val message = (JSONObject(errorStr).getString("message"))
                    mvpView!!.onerror(message)
                }
            } else {
                e.localizedMessage.let {
                    mvpView!!.onError(e.localizedMessage!!)
                }
            }
            Log.e("HandleError", e.message!!)
        } catch (e1: NullPointerException) {
            e1.printStackTrace()
        }
        compositeDisposable.clear()
    }


}