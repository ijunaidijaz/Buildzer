package com.tradesk.utils


import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.SpannableString
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tradesk.R
import com.tradesk.data.entity.LoginModel
import com.tradesk.data.preferences.PreferenceHelper
import com.google.gson.Gson
import java.io.IOException
import java.text.DateFormatSymbols
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class AppUtils @Inject constructor(private val mContext: Context) {

    val isGpsEnabled: Boolean
        get() {
            val manager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    fun checkEmail(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email.trim { it <= ' ' }).matches()
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }


    fun hideSoftKeyboard(view: View) {
        val inputMethodManager =
            mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideSoftKeyboardOut(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken, 0
        )
    }


    fun getLocaleLanguage(): String {
        var lang = "en"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lang = Resources.getSystem().configuration.locales.get(0).language
        } else {
            lang = Resources.getSystem().configuration.locale.language
        }
        if (lang.contains("it", true))
            return "it"
        else
            return "en"
    }

    fun getMonth(month: Int): String {
        return DateFormatSymbols().months[month]
    }

    fun showToast(text: String) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show()
    }

    fun showSuccessToast(text: String) {
        CommonUtil.showSuccessToast(mContext, text)
    }

    fun showErrorToast(text: String) {
        CommonUtil.showErrorToast(mContext, text)
    }

    /* fun showInternetErrorToast(text: String) {
         val mDialogsUtil=DialogsUtil(mContext)
       val  mCustomMsgDialog = mDialogsUtil.showDialog(mContext, R.layout.layout_custom_popup_common_3)
         val mBtnOkayCustomMsgDialog = mCustomMsgDialog.findViewById(R.id.mBtnOkayPopupCommon_3) as RelativeLayout
         val  mTxtOkayCustomMsgDialog = mCustomMsgDialog.findViewById(R.id.mTxtOkayPopupCommon_3) as TextView
         val mTxtMessageCustomMsgDialog = mCustomMsgDialog.findViewById(R.id.mTxtMessagePopupCommon_3) as TextView
         mTxtMessageCustomMsgDialog.text=text
         mTxtOkayCustomMsgDialog.text = mContext.getString(R.string.okay)
         mBtnOkayCustomMsgDialog.setOnClickListener(View.OnClickListener {
             mCustomMsgDialog.dismiss()
         })
         mCustomMsgDialog.show()
     }
 */


    /**
     * show related error MESSAGE to user on api failure
     */
    /* fun showErrorMessage(view: View, t: Throwable) {
         //   showSnackBar(view, getErrorMessage(t));
         CommonUtil.showToast(mContext, getErrorMessage(t))
     }*/

    //return error MESSAGE from webservice error code
/*
    private fun getErrorMessage(throwable: Throwable): String {
        val errorMessage: String
        if (throwable is HttpException || throwable is UnknownHostException
                || throwable is ConnectException) {
            errorMessage = "Something went wrong"
        } else {
            errorMessage = "Unfortunately an error has occurred!"
        }
        return errorMessage
    }
*/

    /**
     * Redirect user to enable GPS
     */
    fun goToGpsSettings() {
        val callGPSSettingIntent = Intent(
            Settings.ACTION_LOCATION_SOURCE_SETTINGS
        )
        mContext.startActivity(callGPSSettingIntent)
    }

    /**
     * check if user has permissions for the asked permissions
     */
    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }


    fun mailGmail(activity: Activity, shareText: String? = "", mailTo: String? = "") {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TITLE, "")
        sharingIntent.putExtra(Intent.EXTRA_EMAIL, mailTo)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        try {
            val pm: PackageManager = activity.packageManager
            val matches: List<ResolveInfo> = pm.queryIntentActivities(sharingIntent, 0)
            var best: ResolveInfo? = null
            for (info in matches) if (info.activityInfo.packageName.endsWith(".gm") ||
                info.activityInfo.name.lowercase(Locale.getDefault()).contains("gmail")
            ) best = info
            if (best != null) sharingIntent.setClassName(
                best.activityInfo.packageName,
                best.activityInfo.name
            )
            activity.startActivity(sharingIntent)
        } catch (e: Exception) {
            activity.startActivity(
                Intent.createChooser(
                    sharingIntent,
                    activity.getString(R.string.app_name)
                )
            )
        }
    }

    fun shareApp(context: Context, msg: String = "") {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            shareIntent.putExtra(Intent.EXTRA_TEXT, msg)
            context.startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            e.toString()
        }
    }


    fun round(datavalue: Double, places: Int): Double {
        var value = datavalue
        if (places < 0) throw IllegalArgumentException()

        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value = value * factor
        val tmp = Math.round(value)
        return tmp.toDouble() / factor
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun changeStatusBarColor(activity: Activity) {
        val window = activity.window
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
        window.statusBarColor = ContextCompat.getColor(activity, R.color.colorPrimary)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun changeStatusBarTranparent(activity: Activity) {
        val window = activity.window
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
//        window.statusBarColor = ContextCompat.getColor(activity, R.color.transparent)
    }

    fun formatPrice(price: String): String {
        val formatter = DecimalFormat("#,###,###")
        return formatter.format(java.lang.Double.valueOf(price))
    }

    companion object {

        /*    public AppUtils(Context context) {
        this.mContext = context;
    }*/

        /**
         * Description : Check if user is online or not
         *
         * @return true if online else false
         */


        private val EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            EMAIL_PATTERN
        )
    }

    fun convertDateFormat(date: String, from: String, to: String): String {
        val spf = SimpleDateFormat(from, Locale.ENGLISH)
        val newDate: Date
        try {
            newDate = spf.parse(date)
            val spf1 = SimpleDateFormat(to, Locale.ENGLISH)
            return spf1.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getCountryAddress(context: Context, latitude: Double, longitude: Double): Address? {
        var geocoder: Geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0)
            }
        } catch (ioe: IOException) {
        }
        return null
    }

    fun getUserData(mPrefs: PreferenceHelper): LoginModel? {
        try {
            val gson = Gson()
            return gson.fromJson(
                mPrefs.getKeyValue(PreferenceConstants.USER_DATA),
                LoginModel::class.java
            )
        } catch (e: Exception) {
            return null
        }
    }

    /* fun getGuestUserData(mPrefs: PreferenceHelper): Result {
         val gson = Gson()
         return gson.fromJson(mPrefs.getKeyValue(PreferenceConstants.GUEST_DATA), Result::class.java)
     }*/

    fun getDate(milliSeconds: Long, dateFormat: String): String {
        // Create a DateFormatter object for displaying date in specified format.
        var formatter: SimpleDateFormat = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        var calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }


//    fun callEvent(mContext: Context, mNumber: String) {
//        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mNumber))
//        (mContext as Activity).startActivity(intent)
//    }


    fun isInternetConnected(): Boolean {
        val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun redirectToAppSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val uri = Uri.fromParts("package", mContext.packageName, null)
        intent.data = uri
        mContext.startActivity(intent)
    }


    /*
        fun getLocalTimeFromUTCTimestamp(timpStampValue: String): String {
            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
            sdf.timeZone = TimeZone.getDefault()
            var date: Date? = null
            var formattedTime = ""
            if(timpStampValue.equals("null", true) || timpStampValue.isEmpty()) {}else{
                var timestamp = java.lang.Long.valueOf(timpStampValue)
                timestamp = timestamp * 1000
                try {
                    date = Date(timestamp)
                    formattedTime = AppConstants.mDateFormatTime.format(date)
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    println("here in date string")
                    try {
                        date = sdf.parse(timestamp.toString())
                    } catch (e1: ParseException) {
                        e1.printStackTrace()
                        e1.printStackTrace()
                    }

                    println(date.toString())
                    formattedTime = AppConstants.mDateFormatTime.format(date) //yyyy/MM/dd HH:mm aa
                }

                */
/*time = formattedTime;
        String format_date = "";
        try {
            SimpleDateFormat timeformat = new SimpleDateFormat("H:mm");
            Date dateObj = timeformat.parse;
            // System.out.println(dateObj);
            format_date = new SimpleDateFormat("K:mm").format(dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }*//*

            return formattedTime
        }
        return ""
    }
*/
/*for showing month name*/
    fun getMonthName(mMonth: Int): String {
        var mMonthName = ""
        when (mMonth) {
            1 -> mMonthName = "jan"
            2 -> mMonthName = "feb"
            3 -> mMonthName = "mar"
            4 -> mMonthName = "apr"
            5 -> mMonthName = "may"
            6 -> mMonthName = "jun"
            7 -> mMonthName = "jul"
            8 -> mMonthName = "aug"
            9 -> mMonthName = "sep"
            10 -> mMonthName = "oct"
            11 -> mMonthName = "nov"
            12 -> mMonthName = "dec"
        }

        return mMonthName

    }

    fun calculateAge(date: Date): Int {
        val dob = Calendar.getInstance()
        dob.timeInMillis = date.time
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--
        }
        return age
    }

}

fun convertDateFormat(date: String): String? {
    var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    var newDate: Date? = Date()
    try {
        newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        spf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return spf.format(newDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return ""
}

fun convertDateFormatWithTime(date: String?): String? {
    var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    var newDate: Date? = Date()
    try {
        newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        spf = SimpleDateFormat("MMM dd, yyyy HH:mm ", Locale.getDefault())
        return spf.format(newDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return ""
}

fun String.toCamelCase(): String {
    if (isEmpty()) return this
    val last = subSequence(1, length).toString()
    val first = this.toCharArray()[0].toString().uppercase(Locale.getDefault())
    return first + last.lowercase(Locale.getDefault())
}

fun gbSpanned(): String {
    return SpannableString("givebackRx").apply {
        spanBold(4, 8)
    }.toString()
}

