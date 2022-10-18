package com.tradesk.appCode.home.leadsDetail.leadsNotesModule


import android.util.Log
import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.NotesListModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject


class LeadsNotesPresenter<V : ILeadNotesView>
@Inject internal constructor(
    private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers),
    ILeadsNotesPresenter<V> {


    fun leadsnoteslist(id: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getLeadNotes(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onListResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onListResponse(it: NotesListModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onNotesListSuccess(it)
            else -> mvpView!!.onError(it.message)
        }
    }


    fun addleadsnotes(ids: String, titles: String, descriptions: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().leadaddnotes(ids, titles, descriptions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onAddResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onAddResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onSuccess(it)
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