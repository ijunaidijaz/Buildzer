package com.tradesk.appCode.notificationsModule


import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.NotifiactionsModel
import com.tradesk.data.entity.ReminderDetailNewModel
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import com.tradesk.utils.PreferenceConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NotificationsPresenter<V : INotificationView>
@Inject internal constructor(private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers), INotificationPresenter<V> {


      fun notifications(page: String,limit: String,isRead: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().notifications(page,limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onHomeResponse(it) }, { this.handleError(it,"Home") })
        )
    }


    private fun onHomeResponse(it: NotifiactionsModel) {
        mvpView!!.hideLoading()
        when(it.status==200){
            true->   mvpView!!.onDrugSearched(it)
            else ->  mvpView!!.onError("Home Failed!")
        }
    }


     fun reminderdetail(id: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().reminderdetail(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onReminderDetailResponse(it) }, { this.handleError(it,"Home") })
        )
    }


    private fun onReminderDetailResponse(it: ReminderDetailNewModel) {
        mvpView!!.hideLoading()
        when(it.status==200){
            true->   mvpView!!.onReminderDetail(it)
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