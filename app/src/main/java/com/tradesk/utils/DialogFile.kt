package com.tradesk.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import com.tradesk.di.ApplicationContext


import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DialogFile @Inject
constructor(@param:ApplicationContext private val mContext: Context) {

    fun showLocationAlert(context: Context) {
        val alertDialog = AlertDialog.Builder(context)

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings")

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings") { dialog, which ->
            dialog.dismiss()
            mContext.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }

        // Showing Alert Message
        alertDialog.show()

    }


    fun showDialogFix(context: Context, layout: Int): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(layout)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER

        dialog.window!!.attributes = lp
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }


}
