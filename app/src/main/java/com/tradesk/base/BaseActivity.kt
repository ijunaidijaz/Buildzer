package com.tradesk.base

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.tradesk.appCode.loginModule.LoginActivity
import com.tradesk.data.entity.LoginModel
import com.tradesk.data.preferences.PreferenceHelper
import com.tradesk.utils.*
import com.tradesk.utils.extension.toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tradesk.BuildzerApp
import com.tradesk.R
import dagger.android.AndroidInjection
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), MvpView, BaseFragment.BaseCallback {

    private var mProgressDialog: ProgressDialog? = null

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

    @Inject
    lateinit var mImageUtility: ImageUtility

    //    @Inject
//    lateinit var permissionFile: PermissionFile
    var userDta: LoginModel? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        performDI()
        super.onCreate(savedInstanceState)

//        val window: Window = this@BaseActivity.window
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
////        window.statusBarColor = Color.BLACK
//        window.statusBarColor = Color.parseColor("#F68D29")
////        window.statusBarColor = Color.parseColor("#61E4E5E6")
////        window.statusBarColor = Color.


        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.parseColor("#F68D29")
        }


        mPrefs.setKeyValue(PreferenceConstants.LANGUAGE_TYPE, mAppUtils.getLocaleLanguage())
    }


    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
    fun showSuccessDialog(msg: String) {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.success_dialog)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        val window: Window? = dialog.getWindow()
        val wlp = window?.attributes
        if (wlp != null) {
            wlp.gravity = Gravity.CENTER
        }
        dialog.setCancelable(true)
        if (wlp != null) {
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        }
        if (window != null) {
            window.attributes = wlp
        }
        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
        val message: TextView = dialog.findViewById<TextView>(R.id.message)
        message.text = msg
    }

    override fun showLoading() {
        hideLoading()
        mProgressDialog = CommonUtil.initProgressDialog(this)

    }

    override fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.cancel()
        }
    }


    fun isInternetConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnectedOrConnecting)
            return true
        else {
            Snackbar.make(findViewById<View>(android.R.id.content), "You are Offline!", 2000).show()
            return false
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

    fun startAnim() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun endAnim() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun setCustomDialog(isSuccess: Boolean, message: String, btn: String) {

//        val dialog = Dialog(this)
//        dialog.setContentView(R.layout.dialog_fail)
//        val width = ConstraintLayout.LayoutParams.MATCH_PARENT
//        val height = ConstraintLayout.LayoutParams.WRAP_CONTENT
//        dialog.window!!.setLayout(width, height)
//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.findViewById<TextView>(R.id.mtvtittle).text=if (isSuccess) "Succuss" else "Fail"
//        dialog.findViewById<TextView>(R.id.mtvDiscription).text=message
//        dialog.findViewById<TextView>(R.id.buttonNo).text=btn
//        dialog.findViewById<TextView>(R.id.buttonNo).setOnClickListener { dialog.dismiss() }
//        dialog.show()

    }

    fun show404Error() {

    }

    override fun onError(resId: Int) {
        onError(getString(resId))
//        show404Error()
    }

    override fun showErrorMessage(message: String) {
        toast(message, false)
    }

    override fun showMessage(message: String) {
        if (true)
            toast(message, true)
        else
            toast("getString(R.string.err)", false)
    }

    override fun showCustomMessage(message: String) {
        CommonUtil.showNoInternet(this, findViewById<View>(android.R.id.content), message)
    }

    override fun showMessage(resId: Int) {
        showMessage(getString(resId))
    }

    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached(tag: String) {

    }


    fun hideAllTypeKB(ibinder: IBinder?) {
        val imm = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(ibinder!!, 0)
    }

    override fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    fun hideKeyboardDialog(view: View? = null) {
        try {
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (view != null)
                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun openActivityOnTokenExpire() {
        //perform actions when groups expire
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.name.startsWith(
                "android.webkit."
            )
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.left - scrcoords[0]
            val y = ev.rawY + view.top - scrcoords[1]
            if (x < view.left || x > view.right || y < view.top || y > view.bottom)
            //  mAppUtils.hideSoftKeyboard(window.decorView.rootView)
                hideKeyboard(window.decorView.rootView)
        }
        return super.dispatchTouchEvent(ev)
    }


    override fun onDestroy() {
        hideLoading()
        super.onDestroy()
    }

    private fun performDI() = AndroidInjection.inject(this)


    override fun enableButton() {

    }

    override fun onLogout(message: String) {
        mAppUtils.showToast("Autherization failed")
        mPrefs.logoutUser()
        Intent(this, LoginActivity::class.java).putExtra(
            "logout",
            "true"
        ).apply {
            startActivity(this)
        }
        finishAffinity()
//        if (message.equals("401")){
//            Toast.makeText(this, "Your session has expired. Please sign in again.", Toast.LENGTH_SHORT).show()
//            mPrefs.logoutUser()
//            Intent(this, LoginEmailPasswordActivity::class.java).apply {
//                startActivity(this)
//            }
//            finishAffinity()
//            startAnim()
//        }else if (message.equals("400")){
//            Toast.makeText(this, "Email does not exist! Please Sign Up!", Toast.LENGTH_SHORT).show()
//            mPrefs.logoutUser()
//            Intent(this, PreSignUpCheckActivity::class.java).apply {
//                startActivity(this)
//            }
//            finishAffinity()
//            startAnim()
//        }else{
//            mPrefs.logoutUser()
//            Intent(this, LoginEmailPasswordActivity::class.java).apply {
//                startActivity(this)
//            }
//            finishAffinity()
//            startAnim()
//        }

    }

    override fun onError(message: String) {
        toast(message, false)
    }

    override fun disableButton() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        endAnim()
    }

    // fused location
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 1000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
//                        toast(lat.toString()+lng.toString())
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
                    exception.startResolutionForResult(this, 123)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e("exception", sendEx.toString())
                }
            }
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


//    private fun convertDateFormat(date: String): String? {
//        var spf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.getDefault())
//        var newDate: Date? = Date()
//        try {
//            newDate = spf.parse(date)
////            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
//            spf = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
//            return spf.format(newDate)
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//        return ""
//    }
}
