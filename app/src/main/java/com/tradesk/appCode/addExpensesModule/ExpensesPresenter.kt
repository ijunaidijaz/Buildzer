package com.tradesk.appCode.addExpensesModule


import android.util.Log
import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.*
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject


class ExpensesPresenter<V : IExpensesView>
@Inject internal constructor(
    private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers),
    IExpensesPresenter<V> {

    fun getExpenseslist(page: String, limit: String, id: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().expenseslist(page, limit, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onResponse(it: ExpensesListModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onList(it)
            else -> mvpView!!.onError(it.message)
        }
    }

    fun deleteexpense(id: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().deleteexpense(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onDeleteResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onDeleteResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onDelete(it)
            else -> mvpView!!.onError(it.message)
        }
    }

    fun addexpense(map: HashMap<String, RequestBody>) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().addexpense(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onAddResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    private fun onAddResponse(it: AddExpenseModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onAdd(it)
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