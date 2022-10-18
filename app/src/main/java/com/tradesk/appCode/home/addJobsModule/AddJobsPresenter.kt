package com.tradesk.appCode.home.addJobsModule


import android.util.Log
import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.SuccessModel
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject


class AddJobsPresenter<V : IAddJobView>
@Inject internal constructor(
    private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers),
    IAddJobPresenter<V> {

    fun add_job(map: HashMap<String, RequestBody>,list: ArrayList<RequestBody?>?) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().addleads(map ,list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onSignInResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }
    fun updateJob(map: HashMap<String, RequestBody>, list: ArrayList<RequestBody?>?) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().updateLeads(map, list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onUpdateResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onSignInResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onAddJob(it)
            else -> mvpView!!.onError(it.message)
        }
    }
    private fun onUpdateResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onUpdateJob(it)
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