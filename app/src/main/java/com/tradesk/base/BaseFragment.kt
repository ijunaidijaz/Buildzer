package com.tradesk.base


import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.IBinder
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.tradesk.appCode.loginModule.LoginActivity
import com.tradesk.data.entity.LoginModel
import com.tradesk.data.preferences.PreferenceHelper
import com.tradesk.utils.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.android.support.AndroidSupportInjection
import java.nio.charset.Charset
import java.util.*
import javax.inject.Inject


/**
 * Created  on 27/09/20.
 */

abstract class BaseFragment : Fragment(), MvpView {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    @Inject
    lateinit var mPrefs: PreferenceHelper

    @Inject
    lateinit var permissionFile: PermissionFile

    @Inject
    lateinit var mAppUtils: AppUtils

    @Inject
    lateinit var mDialogsUtil: DialogsUtil

    var userDta: LoginModel? = null

    var baseActivity: BaseActivity? = null
//        private set

    private var mProgressDialog: ProgressDialog? = null

/*    override val isNetworkConnected: Boolean
        get() = if (baseActivity != null) {
            baseActivity!!.isNetworkConnected
        } else false*/


    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection()
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(view)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            val activity = context as BaseActivity?
            this.baseActivity = activity
            activity!!.onFragmentAttached()
        }
    }

    fun getUserData(): LoginModel? {
        if (userDta == null) {
            try {
                userDta = Gson().fromJson(
                    mPrefs.getKeyValue(PreferenceConstants.USER_DATA),
                    LoginModel::class.java
                )
            } catch (e: Exception) {
                return null
            }
        }
        return userDta
    }

    override fun setCustomDialog(isSuccess: Boolean, message: String, btn: String) {
//        val dialog = Dialog(requireActivity())
//        dialog.setContentView(R.layout.dialog_fail)
//        val width = ConstraintLayout.LayoutParams.MATCH_PARENT
//        val height = ConstraintLayout.LayoutParams.WRAP_CONTENT
//        dialog.window!!.setLayout(width, height)
//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
////        dialog.findViewById<CircleImageView>(R.id.mivFail).loadUserImage("")
//        dialog.findViewById<TextView>(R.id.mtvtittle).text=if (isSuccess) "Succuss" else "Fail"
//        dialog.findViewById<TextView>(R.id.mtvtittle).text
//
//        dialog.findViewById<TextView>(R.id.mtvDiscription).text=message
//        dialog.findViewById<TextView>(R.id.buttonNo).text=btn
//        dialog.findViewById<TextView>(R.id.buttonNo).setOnClickListener {
//
//            dialog.dismiss()
//            startActivity(Intent(requireActivity(),ReferFriendActivity::class.java))
//
//
//        }
//        dialog.show()
    }

    fun isInternetConnected(): Boolean {
        val cm =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnectedOrConnecting)
            return true
        else {
            Snackbar.make(
                requireActivity().findViewById<View>(android.R.id.content),
                "You are Offline! Please check your internet connection",
                2000
            ).show()
            return false
        }

    }


    override fun showLoading() {
        hideLoading()
        mProgressDialog = CommonUtil.initProgressDialog(requireActivity())

    }

    override fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.cancel()
        }
    }

    override fun onError(message: String) {
        if (baseActivity != null) {
            baseActivity!!.onError(message)
        }
    }

    override fun onError(resId: Int) {
        if (baseActivity != null) {
            baseActivity!!.onError(resId)
        }
    }

    override fun showErrorMessage(message: String) {
        if (baseActivity != null) {
            baseActivity!!.showErrorMessage(message)
        }

    }

    override fun showMessage(message: String) {
        if (baseActivity != null) {
            baseActivity!!.showMessage(message)
        }
    }

    override fun showCustomMessage(message: String) {

        if (baseActivity != null) {
            baseActivity!!.showCustomMessage(message)
        }

    }

    override fun showMessage(resId: Int) {
        if (baseActivity != null) {
            baseActivity!!.showMessage(resId)
        }
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun hideKeyboard(view: View) {
        if (baseActivity != null) {
            baseActivity!!.hideKeyboard(view)
        } else {

        }
    }

    fun hideAllTypeKB(binder: IBinder?) {
        if (baseActivity != null) {
            baseActivity!!.hideAllTypeKB(binder!!)
        }
    }

    fun hideKeyboardDialog(view: View? = null) {
        if (baseActivity != null && view != null) {
            baseActivity!!.hideKeyboardDialog(view)
        } else {

        }
    }

    override fun openActivityOnTokenExpire() {
        if (baseActivity != null) {
            baseActivity!!.openActivityOnTokenExpire()
        }
    }


    protected abstract fun setUp(view: View)


    private fun performDependencyInjection() = AndroidSupportInjection.inject(this)


    interface BaseCallback {

        fun onFragmentAttached()

        fun onFragmentDetached(tag: String)
    }

    fun startAnim() {
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun endAnim() {
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onLogout(message: String) {
        mAppUtils.showToast("Autherization failed")
        mPrefs.logoutUser()
        Intent(requireActivity(), LoginActivity::class.java).putExtra(
            "logout",
            "true"
        ).apply {
            startActivity(this)
        }
        requireActivity().finishAffinity()
//        if (message.equals("401")) {
//            Toast.makeText(
//                requireActivity(),
//                "Your session is expired please sign in again.",
//                Toast.LENGTH_SHORT
//            ).show()
//            mPrefs.logoutUser()
//            Intent(requireActivity(), LoginEmailPasswordActivity::class.java).putExtra(
//                "logout",
//                "true"
//            ).apply {
//                startActivity(this)
//            }
//            requireActivity().finishAffinity()
//            startAnim()
//        } else if (message.equals("400")) {
//            Toast.makeText(
//                requireActivity(),
//                "Email does not exist! Please Sign Up!",
//                Toast.LENGTH_SHORT
//            ).show()
//            mPrefs.logoutUser()
//            Intent(requireActivity(), PreSignUpCheckActivity::class.java).putExtra("logout", "true")
//                .apply {
//                    startActivity(this)
//                }
//            requireActivity().finishAffinity()
//            startAnim()
//        } else {
//            mPrefs.logoutUser()
//            Intent(requireActivity(), LoginEmailPasswordActivity::class.java).putExtra(
//                "logout",
//                "true"
//            ).apply {
//                startActivity(this)
//            }
//            requireActivity().finishAffinity()
//            startAnim()
//        }
//
////        startActivity(
////            Intent(activity, SignUpActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
////                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////        )
////        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }


    // fused location
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(api: (() -> Unit)? = null) {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 1000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
//                    for (location in locationResult.locations) {
                    if (locationResult.locations.size > 0) {
                        val lat =
                            locationResult.locations[locationResult.locations.size - 1].latitude
                        val lng =
                            locationResult.locations[locationResult.locations.size - 1].longitude
                        mPrefs.setKeyValue(PreferenceConstants.LAT, lat.toString())
                        mPrefs.setKeyValue(PreferenceConstants.LNG, lng.toString())
                        if (api != null)
                            api()
                        //cal dis , longitude:
//                        Log.e("Distance ", distance(lat, lng, 21.0359851, 105.7804743).toString())
                    }
                    stopLocationUpdates()
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
        task.addOnFailureListener { exception ->
            exception.printStackTrace()
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(requireActivity(), 123)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e("exception", sendEx.toString())
                }
            }
        }
    }

    fun decodeToken(token: String, refreshIfNeeded: () -> Unit) {
        val data: ByteArray = Base64.decode(token, Base64.DEFAULT)
        val text = String(data, Charset.forName("UTF-8"))
        try {
            val membership = org.json.JSONObject(text.substringAfter("}").toString())
                .getString("membership_type")
            if (membership.lowercase(Locale.ENGLISH).equals(
                    mPrefs.getKeyValue(PreferenceConstants.MEMBER_TYPE).lowercase(Locale.ENGLISH)
                ).not()
            ) {
                mPrefs.setKeyValue(PreferenceConstants.MEMBER_TYPE, membership)
                mPrefs.setKeyValue(PreferenceConstants.ACCESSTOKEN, token)
                refreshIfNeeded.invoke()
            }
        } catch (e: Exception) {
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // Function to insert string
    open fun insertString(
        originalString: String,
        stringToBeInserted: String?,
        index: Int
    ): String? {
        // Create a new string
        var newString: String? = String()
        for (i in 0 until originalString.length) {
            // Insert the original string character
            // into the new string
            newString += originalString[i]
            if (i == index) {
                // Insert the string to be inserted
                // into the new string
                newString += stringToBeInserted
            }
        }
        // return the modified String
        return newString
    }
}
