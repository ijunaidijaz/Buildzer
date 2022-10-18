package com.tradesk.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import com.tradesk.R
import javax.inject.Inject

class DialogsUtil @Inject constructor(private val mContext: Context) {
    fun openAlertDialog(
        context: Context, message: String, positiveBtnText: String, negativeBtnText: String,
        listener: OnDialogButtonClickListener
    ) {

        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton(positiveBtnText) { dialog, which ->
            dialog.dismiss()
            listener.onPositiveButtonClicked()

        }

        builder.setNegativeButton(negativeBtnText) { dialog, which ->
            dialog.dismiss()
            listener.onNegativeButtonClicked()

        }
        builder.setTitle(context.resources.getString(R.string.app_name))
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setCancelable(false)
//        builder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(COLOR_I_WANT);
        builder.create().show()
    }

    companion object {
        fun showDialog(context: Context, layout: Int): Dialog {
            val dialog = Dialog(context)
            dialog.setContentView(layout)
            val lp = WindowManager.LayoutParams()
            lp.alpha = 1.0f
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            dialog.window!!.attributes = lp
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(true)
            return dialog
        }
    }

    fun showDialog2(context: Context, layout: Int): Dialog {
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar)
        dialog.setContentView(layout)
        val lp = WindowManager.LayoutParams()
        lp.alpha = 1.0f
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.attributes = lp
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    fun showDialogFix(context: Context, layout: Int): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(layout)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM
        dialog.window!!.attributes = lp
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }


    fun showDialogFullWidth(context: Context, layout: Int): Dialog {
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(layout)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.attributes = lp
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

}