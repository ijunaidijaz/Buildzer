package com.tradesk.appCode.calendarModule


import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.RemindersModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CalendarPresenter<V : ICalendarView>
@Inject internal constructor(private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers), ICalendarPresenter<V> {


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