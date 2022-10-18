package com.tradesk.base


import android.view.View
import com.tradesk.data.entity.ProfileModel

/**
 * Base interface that any class that wants to act as a View in the MVP (Model View Presenter)
 * pattern must implement. Generally this interface will be extended by a more specific interface
 * that then usually will be implemented by an Activity or Fragment.
 */
interface MvpView {

    fun onLogout(message: String)

    fun onGeneratedToken(lastAction: String)

    fun showLoading()

    fun hideLoading()

    fun openActivityOnTokenExpire()

    fun onError(resId: Int)

    fun onError(message: String)

    fun setCustomDialog(isSuccess: Boolean, message: String, btn: String = "OK")

    fun showMessage(message: String)

    fun showErrorMessage(message: String)

    fun showCustomMessage(message: String)

    fun showMessage(resId: Int)

    fun hideKeyboard(view: View)
    fun enableButton()
    fun disableButton()

}
