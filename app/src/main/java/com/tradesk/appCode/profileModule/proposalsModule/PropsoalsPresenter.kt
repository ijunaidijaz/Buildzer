package com.tradesk.appCode.profileModule.proposalsModule


import android.util.Log
import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.*
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.http.Multipart
import javax.inject.Inject


class PropsoalsPresenter<V : IProposalsView>
@Inject internal constructor(
    private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers),
    IProposalsPresenter<V> {

    fun getProposals(page: String, limit: String, status: String, type: String, id: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getproposals(page, limit, status, type, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }
    fun getProfile() {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ this.onResponse(it) }, { this.handleError(it, "Home") })
        )
    }


    private fun onResponse(it: ProfileModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> this.onProfile(it)
            else -> mvpView!!.onError("Profile Error")
        }
    }
    //
    private fun onResponse(it: PorposalsListModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onList(it)
            else -> mvpView!!.onError(it.message)
        }
    }

    fun getProposalItemslist() {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().proposalItemslist()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onDefaultResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }
    fun changeStatus(status: String,type: String,id: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().changeProposalStatus(id,status,type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onStatusResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }
    //
    private fun onDefaultResponse(it: DefaultItemsModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onDefaultList(it)
            else -> mvpView!!.onError(it.message)
        }
    }

    fun sendProposal(id: String, email: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().sendproposal(id, email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onSendResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }
    private fun onProfile(it: ProfileModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onProfile(it)
            else -> mvpView!!.onError(it.message)
        }
    }
    //
    private fun onSendResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onSend(it)
            else -> mvpView!!.onError(it.message)
        }
    }
    private fun onStatusResponse(it: ChangeProposalStatus) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onChangeStatus(it)
            else -> mvpView!!.onError(it.message)
        }
    }
    fun deleteproposal(id: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().deleteproposal(id)
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

    fun addProposals(map: HashMap<String, RequestBody>/*, items: ArrayList<AddItemDataUpdate>*/) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().addproposal(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onAddResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }
    fun updateProposal(map: HashMap<String, RequestBody>/*, items: ArrayList<AddItemDataUpdate>*/) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().updateProposal(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onAddResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }
    fun addProposals(map: HashMap<String, RequestBody>, list: ArrayList<RequestBody>?,images:ArrayList<MultipartBody.Part>) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().addproposal(map,list,images)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onAddResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }
//    fun updateProposal(map: HashMap<String, RequestBody>/*, items: ArrayList<AddItemDataUpdate>*/,list: ArrayList<RequestBody>?,images:ArrayList<MultipartBody.Part>) {
//        mvpView!!.showLoading()
//        cmp.add(
//            apiHelpers.restService().updateProposal(map,list,images)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(
//                    { this.onAddResponse(it) },
//                    { this.handleError(it, "preSignUpVerification") })
//        )
//    }
    fun updateProposal(map: HashMap<String, RequestBody>,list: ArrayList<RequestBody>?,images:ArrayList<MultipartBody.Part>,existingDocs: ArrayList<RequestBody>?) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().updateProposal(map,list,existingDocs,images)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onAddResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }
    private fun onAddResponse(it: AddProposalsModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onAdd(it)
            else -> mvpView!!.onError(it.message)
        }
    }

    fun getDetail(id: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getProposalDetail(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onDetailResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onDetailResponse(it: ProposalDetailModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onDetails(it)
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
                } else if (e.code() == 503 || e.code() == 500) {
                    mvpView!!.onError("Service unavailable " + e.code().toString())
                } else {
                    val errorStr = responseBody!!.string()
                    val message = (JSONObject(errorStr).getString("message"))
                    if (message != null) mvpView!!.onError(message) else {
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