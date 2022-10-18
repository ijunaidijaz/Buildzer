package com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule


import android.util.Log
import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.AddConversationModel
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.GetConversationModel
import com.tradesk.data.entity.LeadsModel
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import com.tradesk.utils.PreferenceConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class ChatPresenter<V : IChatView>
@Inject internal constructor(
    private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers), IChatPresenter<V> {


    fun getChat(map: HashMap<String, String>) {
        if (map.containsKey("dateTime").not()) mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getConversation(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onGetChatResponse(it, map.containsKey("dateTime")) },
                    { this.handleError(it, "Home") })
        )
    }

    fun getChatUsers(page: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getChatleads("true", page, limit = "15")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onJobsResponse(it) }, { this.handleError(it, "Home") })
        )
    }


    private fun onJobsResponse(it: LeadsModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onChatUsersList(it)
            else -> mvpView!!.onError("Home Failed!")
        }
    }

    private fun onGetChatResponse(it: GetConversationModel, isRecent: Boolean) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> {
                if (isRecent)
                    mvpView!!.onGetRecentChatResp(it.data)
                else
                    mvpView!!.onGetChatResp(it.data)
            }
            else -> mvpView!!.onError("Home Failed!")
        }
    }


    fun addMsg(map: HashMap<String, String>) {
//        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().addConversationText(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onAddMsgResponse(it) }, { this.handleError(it, "Home") })
        )
    }

    fun addImage(map: HashMap<String, RequestBody>) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().addConversation(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onAddMsgResponse(it) }, { this.handleError(it, "Home") })
        )
    }


    private fun onAddMsgResponse(it: AddConversationModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onAddMsgResp(it)
            else -> mvpView!!.onError("Home Failed!")
        }
    }


    fun appVersion() {
        val request =
            if (mPrefs.isUserLoggedIn(PreferenceConstants.USER_LOGGED_IN)) apiHelpers.restService()
                .appVersion(mPrefs.getKeyValue(PreferenceConstants.MEMBER_TYPE))
            else apiHelpers.restService().appVersion()
        cmp.add(
            request.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onAppVersionResp(it) }, { this.handleError(it, "Version") })
        )
    }


    private fun onAppVersionResp(it: AppVersionEntity) {
        when (it.success) {
            true -> mvpView!!.onAppVersionResp(it)
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