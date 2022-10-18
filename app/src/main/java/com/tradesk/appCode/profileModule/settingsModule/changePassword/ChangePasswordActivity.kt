package com.tradesk.appCode.profileModule.settingsModule.changePassword

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.tradesk.R
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.SuccessModel
import com.tradesk.data.entity.TermsModel
import com.tradesk.utils.AppConstants
import com.tradesk.utils.extension.toast
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_change_password.etCPassword
import javax.inject.Inject

import java.util.Base64

class ChangePasswordActivity : BaseActivity(), IChangePasswordView {
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

    override fun onSuccess(it: SuccessModel) {
        toast(it.message)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTermsSuccess(it: TermsModel) {



        if(intent.getStringExtra("type").equals("Terms $ Conditions")){
            val decodedBytes = Base64.getDecoder().decode(it.data.terms_condition.description)
            val decodedString = String(decodedBytes)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                terms.setText(Html.fromHtml(decodedString, Html.FROM_HTML_MODE_COMPACT));
            } else {
                terms.setText(Html.fromHtml(decodedString));
            }
        }else if (intent.getStringExtra("type").equals("Privacy Policy")){
            val decodedBytes = Base64.getDecoder().decode(it.data.privacy_policy.description)
            val decodedString = String(decodedBytes)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                terms.setText(Html.fromHtml(decodedString, Html.FROM_HTML_MODE_COMPACT));
            } else {
                terms.setText(Html.fromHtml(decodedString));
            }
        }



    }

    override fun onerror(it: String) {
        toast(it)
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    @Inject
    lateinit var presenter: ChangePasswordPresenter<IChangePasswordView>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        presenter.onAttach(this)

        mIvBack.setOnClickListener { finish() }
        textView6.setText(intent.getStringExtra("type").toString())
        if (intent.getStringExtra("type").equals("Change Password")){
            constraintLayout17.visibility=View.VISIBLE
            terms.visibility=View.GONE
        }else if(intent.getStringExtra("type").equals("Terms $ Conditions")){
            constraintLayout17.visibility=View.GONE
            terms.visibility=View.VISIBLE
            if (isInternetConnected()){
                presenter.terms()
            }
        }else if (intent.getStringExtra("type").equals("Privacy Policy")){
            constraintLayout17.visibility=View.GONE
            terms.visibility=View.VISIBLE
            if (isInternetConnected()){
                presenter.terms()
            }
        }


        ivSubmit.setOnClickListener {
             if (etOldPassword.text.toString().trim().isEmpty()) {
            toast("Enter old password", false)
        }  else if (!AppConstants.PATTERN.matcher(etOldPassword.text.toString().trim()).matches()) {
            toast("Password must contain upper and lower letters, numerics, and a special character with a min of 8 characters", false)
        }  else if (etCPassword.text.toString().trim().isEmpty()) {
            toast("Enter new password", false)
        }  else if (!AppConstants.PATTERN.matcher(etCPassword.text.toString().trim()).matches()) {
                 toast("Password must contain upper and lower letters, numerics, and a special character with a min of 8 characters", false)
        }  else if (etConfirmPassword.text.toString().trim().isEmpty()) {
                 toast("Enter confirm password", false)
        }  else if (etConfirmPassword.text.toString().trim().equals(etCPassword.text.toString().trim()).not()) {
            toast("Password does not match", false)
        }else{
                 if (isInternetConnected())
                     presenter.changepassword(etOldPassword.text.toString().trim(),etCPassword.text.toString().trim())
             }

        }
    }
}