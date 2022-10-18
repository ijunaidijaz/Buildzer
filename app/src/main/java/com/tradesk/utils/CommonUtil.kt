package com.tradesk.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.tradesk.R
import com.tradesk.data.entity.Client
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Predicate
import javax.inject.Singleton
import kotlin.collections.ArrayList


@SuppressLint("StaticFieldLeak")
@Singleton
object CommonUtil {

    var proposalType: String = "";
    private lateinit var mLoader: ImageView
    private lateinit var rl: RelativeLayout
    const val SimpleDateFormatDashs = "MM-dd-yyyy"
    const val ServerDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val TimeFormat = "hh:mm a"
    fun log(content: String) {
        Log.d("tien.hien", content)
    }

    fun setContext(context: Context) {
        val layout =
            (context as Activity).findViewById<View>(android.R.id.content).rootView as ViewGroup

        val imgparams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        mLoader = ImageView(context, null)
//        mLoader.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.loader))
        mLoader.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.rotate)
        )
        mLoader.layoutParams = imgparams

        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )

        rl = RelativeLayout(context)

        rl.gravity = Gravity.CENTER
        rl.addView(mLoader)
        rl.setOnClickListener { v -> }

        layout.addView(rl, params)
        hideProgress()
    }

    fun showSuccessDialog(activity: Activity, msg: String) {
        var dialog = Dialog(activity)
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

    fun createPartFromArray(param: ArrayList<String>): ArrayList<RequestBody?>? {
        val d: ArrayList<RequestBody?> = arrayListOf()
        for (p in param) {
            d.add(createPartFromString(p))
        }
        return d
    }

    fun createBodyString(string: String): RequestBody {
        return RequestBody.create(MediaType.parse("text"), string)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun containsId(list: List<Client>, id: String?): Boolean {
        return list.stream().filter(Predicate<Client> { o: Client ->
            o._id.equals(id)
        }).findFirst().isPresent()
    }

    fun createPartFromString(param: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), param)
    }

    fun getParts(files: List<File>, key: String): ArrayList<MultipartBody.Part> {
        val surveyImagesParts = arrayListOf<MultipartBody.Part>()

        for (index in files.indices) {
            Log.d(
                "TAG",
                "requestUploadSurvey: survey image " + index + "  " + files.get(index).absolutePath
            )
            val file = files.get(index)
            val surveyBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
            surveyImagesParts.add(MultipartBody.Part.createFormData(key, file.name, surveyBody))
        }
        return surveyImagesParts;
    }

    fun showProgress() {
        rl.isClickable = true
        rl.visibility = View.VISIBLE
        mLoader.visibility = View.VISIBLE
    }

    @Throws(IOException::class)
    fun streamToString(inputStream: InputStream?): String {
        var str = ""
        if (inputStream != null) {
            val sb = StringBuilder()
            try {
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line = reader.readLine()
                while (line != null) {
                    sb.append(line)
                    line = reader.readLine()
                }
                reader.close()
            } finally {
                inputStream.close()
            }
            str = sb.toString()
        }
        return str
    }

    fun hideProgress() {
        rl.isClickable = false
        rl.visibility = View.GONE
        mLoader.visibility = View.INVISIBLE
    }

//  it.getIndeterminateDrawable().setColorFilter(Color.parseColor("#53CBF1"), android.graphics.PorterDuff.Mode.SRC_IN);


    fun showSuccessToast(context: Context, msg: String) {
//        Toasty.success(context, msg, Toast.LENGTH_LONG).show()
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun showErrorToast(context: Context, msg: String) {
//        Toasty.error(context, msg, Toast.LENGTH_LONG).show()
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

    }

    fun showCenterErrorToast(context: Context, msg: String) {
//        val t = Toasty.error(context, msg, Toast.LENGTH_LONG)
//        t.setGravity(Gravity.TOP, 0, 0)
//        t.show()
        val toast = Toast.makeText(context, msg, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    fun showNoInternet(context: Context?, view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.duration = 2000
        snackbar.show()
    }

    fun showTopBarMsg(context: Context?, view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.duration = 2000
        val v = snackbar.view
        val params = v.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snackbar.show()
    }

    fun showServerError(context: Context?, view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.duration = 10000
        snackbar.show()
    }

    fun convertDatesFormat(date: String, from: String, to: String): String {

        var spf = SimpleDateFormat(from, Locale.getDefault())
        val newDate: Date
        try {
            newDate = spf.parse(date)
            spf = SimpleDateFormat(to, Locale.getDefault())

            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }


    fun convertTimeFormat(date: String): String {
        var spf = SimpleDateFormat("hh:mm", Locale.getDefault())
        val newDate: Date
        try {
            newDate = spf.parse(date)
            spf = SimpleDateFormat("hh:mm a", Locale.getDefault())


            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun convertDateFormat(date: String): String {
        var spf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val newDate: Date
        try {
            newDate = spf.parse(date)
            spf = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun removeDollar(amount: String): String {
        val amt = amount.replace("$", "")
        return amt.replace(" ", "")
    }

    fun twoDigitAfterPoint(value: String): String {
        var v = "0"
        if (value.isEmpty())
            v = "0"
        else
            v = value
        return "$${(String.format(Locale.getDefault(), "%.2f", v.toDouble()))}"
    }

    /* fun popUpAlert(context: Context, msg: String, popupDialog: PopupDialogInterface) {
         val builder = AlertDialog.Builder(context)
         builder.setTitle(context.getString(R.string.app_name))
         builder.setMessage(msg)
                 .setCancelable(true)
                 .setPositiveButton("Ok") { dialog, which ->
                     dialog.dismiss()
                     //popupDialog.onPositiveClick()
                 }
         builder.setOnDismissListener {
             popupDialog.onPositiveClick()
         }
         val dialog = builder.create()
         dialog.show()
 //        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black))
     }

     fun popUpAlertConst(context: Context, msg: String, popupDialog: PopupDialogInterface) {
         val builder = AlertDialog.Builder(context)
         builder.setTitle(context.getString(R.string.app_name))
         builder.setMessage(msg)
                 .setCancelable(false)
                 .setPositiveButton("Ok") { dialog, which ->
                     dialog.dismiss()
                     popupDialog.onPositiveClick()
                 }
         val dialog = builder.create()
         dialog.show()
 //        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black))
     }*/

    fun permissionAlert(context: Context, msg: String, onBack: (() -> Unit)? = null) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.app_name))
        builder.setMessage(msg)
            .setCancelable(true)
            .setPositiveButton("Enable") { dialog, which ->
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
        builder.setOnDismissListener {
            onBack?.invoke()
        }
        val dialog = builder.create()
        dialog.show()
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black))
    }

/*
    fun popUpDialog(context: Context, msg: String, popupDialog: PopupDialogInterface) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, which ->
                    dialog.dismiss()
                    popupDialog.onPositiveClick()
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                    popupDialog.onNegativeClick()
                }

        val dialog = builder.create()
        dialog.show()
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black))
//        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black))
    }
*/

/*
    fun popUpDialog(context: Context, posBtn: String, negBtn: String, msg: String, popupDialog: PopupDialogInterface) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(posBtn) { dialog, which ->
                    dialog.dismiss()
                    popupDialog.onPositiveClick()
                }
                .setNegativeButton(negBtn) { dialog, which ->
                    dialog.dismiss()
                    popupDialog.onNegativeClick()
                }

        val dialog = builder.create()
        dialog.show()
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black))
//        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black))
    }
*/


    fun getCurrentDate(timpStampValue: String, sdf: SimpleDateFormat): String {
        val targetFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var date: Date? = null
        var formattedDate = ""
        var timestamp = timpStampValue.toLong()
        timestamp = timestamp * 1000
        try {
            date = Date(timestamp)
            formattedDate = targetFormat.format(date)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            System.out.println("here in date string")
            try {
                date = sdf.parse(timestamp.toString())
            } catch (e1: ParseException) {
                e1.printStackTrace()
            }
            formattedDate = targetFormat.format(date) //yyyy-MM-dd HH:mm aa
        }
        return formattedDate
    }

    fun convertDateFormatToLong(date: String, format: String): Long {
        val spf = SimpleDateFormat(format, Locale.getDefault())
        val newDate: Date
        try {
            newDate = spf.parse(date)
            val cal = Calendar.getInstance()
            cal.timeInMillis = newDate.time
            return cal.timeInMillis
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    fun getCurrentTime(timpStampValue: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timpStampValue

        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currenTimeZone = calendar.time
        return sdf.format(currenTimeZone)

    }

    fun convertDate(timpStampValue: Long, format: String): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timpStampValue
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val currenTimeZone = calendar.time
        return sdf.format(currenTimeZone)

    }


    fun convertDateWithFormats(date: String, formatFrom: String, formatTo: String): String {
        var spf = SimpleDateFormat(formatFrom, Locale.getDefault())
        val newDate: Date
        try {
            newDate = spf.parse(date)//dd-MMM
            spf = SimpleDateFormat(formatTo, Locale.getDefault())

            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }


    fun initProgressDialog(context: Context): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.show()
        if (progressDialog.window != null) {
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        progressDialog.setContentView(R.layout.progress_dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        return progressDialog
    }

    fun getFormatedDateString(STAMP: String?): String? {
        val from = SimpleDateFormat(ServerDateTimeFormat)
        val to =
            SimpleDateFormat(SimpleDateFormatDashs)
        var formattedDate: Date? = null
        try {
            val date = from.parse(STAMP)
            formattedDate = date
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return to.format(formattedDate)
    }
    fun getCurrentTimeOnly(): String? {
        val calendar = Calendar.getInstance()
        val mdformat = SimpleDateFormat(TimeFormat)
        return mdformat.format(calendar.time)
    }
}