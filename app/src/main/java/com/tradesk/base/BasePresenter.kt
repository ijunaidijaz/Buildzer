package com.tradesk.base


import android.util.Log
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.network.RestService
import com.tradesk.data.preferences.PreferenceHelper
import com.tradesk.utils.NetworkConstants
import com.tradesk.utils.PreferenceConstants
import com.google.gson.JsonObject
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject
import retrofit2.HttpException

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * onAttach() and onDetach(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
open class BasePresenter<V : MvpView>
constructor(
    private val dataManager: PreferenceHelper,
    val compositeDisposable: CompositeDisposable, private val apiHelper: ApiHelper
) : MvpPresenter<V> {

    var mvpView: V? = null
        private set

    val isViewAttached: Boolean
        get() = mvpView != null

    override fun onAttach(mvpView: V) {
        this.mvpView = mvpView
    }

    override fun onDetach() {
        mvpView!!.hideLoading()
        compositeDisposable.dispose()
        mvpView = null
    }

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    fun apiService(): RestService? {
        return apiHelper.restService()
    }

    override fun handleApiError(errorType: Int, message: String) {
        when (errorType) {
            NetworkConstants.AUTHFAIL -> {
                mvpView!!.onError(message)
                dataManager.logoutUser()
                mvpView!!.openActivityOnTokenExpire()
            }
            else -> mvpView!!.onError(message)
        }
    }

    override fun setUserAsLoggedOut() {}

    override fun authDetail(lastAction: String) {
        dataManager.setGotToken(PreferenceConstants.GotToken, false)
        mvpView!!.showLoading()
        val jo = JsonObject()
        jo.addProperty("API_Key__c", "e4ac15dd-3458-4470-951a-e4df15033842")
//        compositeDisposable.add(
//            apiHelper.authdetailService().auth_detail(jo)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe({ this.OnAuthDetail(it, lastAction) }, { handleError(it, "") })
//        )
    }

    fun OnAuthDetail(it: String, lastAction: String) {
//        mvpView!!.hideLoading()
//        if (it.success) {
//            if ("/".equals(it.data.URL_Suffix__c.get(it.data.URL_Suffix__c.length-1)))
//                dataManager.setKeyValue(PreferenceConstants.BASE_URL, it.data.URL_Suffix__c)
//            else
//                dataManager.setKeyValue(PreferenceConstants.BASE_URL, it.data.URL_Suffix__c + "/")
//            generateToken(it, lastAction)
//        } else {
//            mvpView!!.onError("Something went wrong. Please try later!")
//        }
    }

    override fun generateToken(it: String, lastAction: String) {
//        mvpView!!.showLoading()
//        compositeDisposable.add(
//            apiHelper.restService().generate_token(it.data.Grant_Type__c ,it.data.Client_Id__c ,it.data.Client_Secret__c
//                ,it.data.Username__c ,it.data.Password__c)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe({ this.OnGenerateToken(it, lastAction) }, { handleError(it, "") })
//        )
    }

    override fun logoutUser(lastAction: String) {
//        mvpView!!.showLoading()
        mvpView!!.onLogout("")
//        compositeDisposable.add(
//            apiHelper.restService().revoke(dataManager.getKeyValue(PreferenceConstants.ACCESSTOKEN))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe({ this.OnLogoutResponse(it, lastAction) }, { handleError(it, lastAction) })
//        )
    }

    fun OnLogoutResponse(it: String, lastAction: String) {
        mvpView!!.hideLoading()
        try {
            mvpView!!.onLogout("")
        } catch (e: Exception) {
        }
    }


    fun OnGenerateToken(
        it: String,
        lastAction: String
    ) {
        mvpView!!.hideLoading()
        try {
//            dataManager.setGotToken(PreferenceConstants.GotToken, true)
//            dataManager.setKeyValue(PreferenceConstants.ACCESSTOKEN, it.access_token)
//            dataManager.setKeyValue(PreferenceConstants.TOKENTYPE, it.token_type)
        } catch (e: Exception) {
        }
        mvpView!!.onGeneratedToken(lastAction)
    }

    open fun handleError(e: Throwable, lastAction: String) {
        e.printStackTrace()
        try {
            mvpView!!.hideLoading()
            if (lastAction.equals("logout")) {
                dataManager.logoutUser()
                mvpView!!.onLogout("")
                return
            }
            if (e is HttpException) {
                val responseBody = e.response()!!.errorBody()
                if (e.code() == 401 || e.localizedMessage!!.contains("401 Unauthorized", false)) {
//                    android.os.Handler().postDelayed({
//                        authDetail(lastAction)
//                    }, 1200)
                    mvpView!!.onLogout("401")
                } /*else if (e.code() == 400) {
                    mvpView!!.onLogout("400")
//                    mvpView!!.showErrorMessage(getErrorMessage(responseBody))
                }*/ else {
                    mvpView!!.showErrorMessage(getErrorMessage(responseBody))
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

    fun getErrorMessage(responseBody: okhttp3.ResponseBody?): String {
        try {
            val jsonObject = JSONObject(responseBody!!.string())
            if (jsonObject.has("error_description"))
                return jsonObject.getString("error_description")
            else
                return jsonObject.getString("message")
        } catch (e: Exception) {
            return e.message!!
        }
    }

    class MvpViewNotAttachedException :
        RuntimeException("Please call .onAtPresentertach(MvpView) before" + " requesting data to the Presenter")
}