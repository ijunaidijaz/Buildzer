package com.tradesk.appCode.jobsModule


import android.util.Log
import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.LeadsModel
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import com.tradesk.utils.PreferenceConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class JobsPresenter<V : IJobsView>
@Inject internal constructor(private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers), IJobsPresenter<V> {


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
            else -> mvpView!!.onError("Home Failed!")
        }
    }

    override fun handleError(e: Throwable, lastAction: String) {
        e.printStackTrace()
        try {
            mvpView!!.hideLoading()

            if (e is HttpException) {
                val responseBody = e.response()!!.errorBody()
                if (e.code() == 401 || e.localizedMessage!!.contains("401 Unauthorized", false)) {
//                    mvpView!!.showErrorMessage(getErrorMessage(responseBody))
                    mvpView!!.onLogout(getErrorMessage(responseBody))
                } else if (e.code() == 400) {

                    val errorStr = responseBody!!.string()
                    val message = (JSONObject(errorStr).getString("message"))
                    mvpView!!.onError(message)
                }else if (e.code() == 503||e.code() == 500){
                    mvpView!!.onError("Service unavailable "+e.code().toString())
                } else{
                    val errorStr = responseBody!!.string()
                    val message = (JSONObject(errorStr).getString("message"))
                   if (message!=null) mvpView!!.onError(message)else{
                       mvpView!!.onError("Something went wrong")
                   }
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