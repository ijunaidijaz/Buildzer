package com.tradesk.appCode.home.leadsDetail


import android.util.Log
import com.tradesk.appCode.home.leadsDetail.leadsNotesModule.ILeadDetailView
import com.tradesk.base.BasePresenter
import com.tradesk.data.entity.AdditionalImagesWithClientModel
import com.tradesk.data.entity.LeadDetailModel
import com.tradesk.data.entity.ProfileModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.data.network.ApiHelper
import com.tradesk.data.preferences.AppPreferenceHelper
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject


class LeadDetailPresenter<V : ILeadDetailView>
@Inject internal constructor(
    private val mPrefs: AppPreferenceHelper, val cmp: CompositeDisposable, val apiHelpers: ApiHelper
) : BasePresenter<V>(mPrefs, compositeDisposable = cmp, apiHelper = apiHelpers),
    ILeadDetailPresenter<V> {

    fun getLeadDetail(id: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getLeadDetail(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onResponse(it: LeadDetailModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onLeadDetail(it)
            else -> mvpView!!.onError(it.message)
        }
    }

    fun deleteimages(data: JsonObject) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().deleteimages(data)
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
            200 -> mvpView!!.onLeadDeleteStatus(it)
            else -> mvpView!!.onError(it.message)
        }
    }

    fun deleteprofiledocs(docurl: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().deleteprofiledocs(docurl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onDocDeleteResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onDocDeleteResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onDocDeleteStatus(it)
            else -> mvpView!!.onError(it.message)
        }
    }

    fun convertleads(id: String, type: String, status: String, converted_to_job: String) {
        if (status.equals("complete", true)||status.equals("completed",true)) {
            mvpView!!.showLoading()
            cmp.add(
                apiHelpers.restService().convertleads(id, type, "completed", converted_to_job)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        { this.onStatusResponse(it) },
                        { this.handleError(it, "preSignUpVerification") })
            )
        } else {
            mvpView!!.showLoading()
            cmp.add(
                apiHelpers.restService().convertleads(id, type, status, converted_to_job)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        { this.onStatusResponse(it) },
                        { this.handleError(it, "preSignUpVerification") })
            )
        }
    }

    //
    private fun onStatusResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onStatus(it)
            else -> mvpView!!.onError(it.message)
        }
    }

    fun junkleads(id: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getLeadDelete(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onJunkResponse(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onJunkResponse(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onLeadDeleteStatus(it)
            else -> mvpView!!.onError(it.message)
        }
    }

    fun addreminder(
        id: String,
        client_id: String,
        remainder_type: String,
        type: String,
        dateTime: String,
        timezone: String
    ) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService()
                .addreminder(id, client_id, remainder_type, type, dateTime, timezone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onAddReminder(it) },
                    { this.handleError(it, "preSignUpVerification") })
        )
    }

    //
    private fun onAddReminder(it: SuccessModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onAddReminder(it)
            else -> mvpView!!.onError(it.message)
        }
    }

    fun addImgaes(map: HashMap<String, RequestBody>) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().add_addtional_images(map)
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
            200 -> mvpView!!.onAddImage(it)
            else -> mvpView!!.onError(it.message)
        }
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
            200 -> mvpView!!.onProfileData(it)
            else -> mvpView!!.onError("Home Failed!")
        }
    }

    fun getadditionalimagesjobs(page: String, limit: String, status: String) {
        mvpView!!.showLoading()
        cmp.add(
            apiHelpers.restService().getadditionalimagesjobs(page, limit, status)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { this.onAdditionalImagesResponse(it) },
                    { this.handleError(it, "Home") })
        )
    }


    private fun onAdditionalImagesResponse(it: AdditionalImagesWithClientModel) {
        mvpView!!.hideLoading()
        when (it.status) {
            200 -> mvpView!!.onAdditionalImagesData(it)
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