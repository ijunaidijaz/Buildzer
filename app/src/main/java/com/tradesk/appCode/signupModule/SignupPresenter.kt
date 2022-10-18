package com.tradesk.appCode.signupModule


import android.util.Log
import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.LoginModel
import com.tradesk.data.entity.SignupModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject


class SignupPresenter<V : ISignUpView>
@Inject internal constructor(
    private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers),
    ISignupPresenter<V> {


    fun signup(name:String,email:String,addres:String,password:String,userType:String,city:String,state:String,postal_code:String,latLong:String,trade:String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().signup(name,email,addres,password,userType,city,state,postal_code,trade,latLong)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onSignInResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }
    fun socialLogin(
        address: String,
        accessToken: String,
        device_token: String,
        device_type: String,
        post_code: String,
        city: String,
        state: String,
        latLong: String,
        loginType: String
    ) {
        if (loginType.equals("google", true)) {
            mvpView!!.showLoading()
            cmp.add(
                apiHelpers.restService().socialLogin(
                    address,
                    accessToken,
                    device_token,
                    device_type,
                    "1",
                    city,
                    state,
                    post_code,
                    "unknown",
                    latLong
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        { this.onSignInResponse(it) },
                        { this.handleError(it, "preSignUpVerification") })
            )
        }else{

            mvpView!!.showLoading()
            cmp.add(
                apiHelpers.restService().fbLogin(
                    address,
                    accessToken,
                    device_token,
                    device_type,
                    "1",
                    city,
                    state,
                    post_code,
                    "unknown",
                    latLong
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        { this.onSignInResponse(it) },
                        { this.handleError(it, "preSignUpVerification") })
            )
        }
    }

    //
    private fun onSignInResponse(it: SignupModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onSignup(it)
            else -> mvpView!!.onError(it.message)
        }
    }
    private fun onSignInResponse(it: LoginModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onSocialSignup(it)
            else -> mvpView!!.onError(it.message)
        }
    }
    fun forgot( email:String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().forgot(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onForgotResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onForgotResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onForgot(it)
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