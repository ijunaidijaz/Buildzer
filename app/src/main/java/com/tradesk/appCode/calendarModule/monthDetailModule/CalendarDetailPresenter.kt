package com.tradesk.appCode.calendarModule.monthDetailModule


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


class CalendarDetailPresenter<V : ICalendarDetqilView>
@Inject internal constructor(
    private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers),
    ICalendarDetailPresenter<V> {

    fun calendardetail(timezone: String, date: String, dateType: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().calendardetail(timezone, date, dateType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onResponse(it: CalendarDetailModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onList(it)
            else -> mvpView!!.onError(it.message)
        }
    }


    fun getremindersdate(date: String,page: String,limit:String,timezone:String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getremindersdate("date",date,page,limit,timezone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onHomeResponse(it) }, { this.handleError(it,"Home") })
        )
    }


    private fun onHomeResponse(it: RemindersModel) {
        mvpView!!.hideLoading()
        when(it.status){
            200->   mvpView!!.onDrugSearched(it)
            else ->  mvpView!!.onError("Home Failed!")
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

    fun addreminder(  remainder_type: String,type: String, dateTime: String, description: String, timezone: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().addreminderCal( remainder_type, type, dateTime,description,timezone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onAddReminder(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onAddReminder(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onAddReminder(it)
            else -> mvpView!!.onError(it.message)
        }
    }
}