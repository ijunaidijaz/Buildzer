package com.tradesk.appCode.home.salePersonModule


import android.util.Log
import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.ClientSalesModelNew
import com.tradesk.data.entity.ClientsListModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.data.entity.TradesModel
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject


class AddSalesPresenter<V : IAddSalesView>
@Inject internal constructor(
    private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers),
    ISalesPresenter<V> {

    fun add_sales(map: HashMap<String, RequestBody>) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().addclient(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onSignInResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onSignInResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onAddSales(it)
            else -> mvpView!!.onError(it.message)
        }
    }


    fun getDetails(id: String, page: String, limit: String, type: String, status: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().clientdetails(id, page, limit, type, status)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onDetailResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onDetailResponse(it: ClientSalesModelNew) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onDetails(it)
            else -> mvpView!!.onError(it.message)
        }
    }


    fun getTradeDetails(type: String, page: String, limit: String) {
//        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().tradedetails(type, page, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onTradeDetailResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onTradeDetailResponse(it: TradesModel) {
//        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onTradesDetails(it)
            else -> mvpView!!.onError(it.message)
        }
    }


    fun updatesaleclient(map: HashMap<String, RequestBody>) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().updatesaleclient(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onUpdateResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onUpdateResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onUpdateSales(it)
            else -> mvpView!!.onError(it.message)
        }
    }


    fun addjob_sales(client: String, jobId: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().addjobsubusers(client, jobId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onJobUserResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onJobUserResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onAddJobSales(it)
            else -> mvpView!!.onError(it.message)
        }
    }


    fun addsubusers(map: HashMap<String, RequestBody>) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().addsubusers(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onAddSubResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onAddSubResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onAddSubUsers(it)
            else -> mvpView!!.onError(it.message)
        }
    }


    fun get_sales(page: String, limit: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().clientslist("sales", page, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onSalesResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onSalesResponse(it: ClientsListModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onSalesList(it)
            else -> mvpView!!.onError(it.message)
        }
    }


    fun get_all_sales(page: String, limit: String, trade: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().saleslist("sales", page, limit, trade)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onAllSalesResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onAllSalesResponse(it: ClientsListModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onSalesList(it)
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