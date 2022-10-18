package com.tradesk.appCode.forgotPassword

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.tradesk.R
import com.tradesk.appCode.signupModule.ISignUpView
import com.tradesk.appCode.signupModule.SignupPresenter
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.LoginModel
import com.tradesk.data.entity.SignupModel
import com.tradesk.data.entity.SuccessModel
import com.tradesk.utils.extension.toast
import kotlinx.android.synthetic.main.activity_forgot.*
import javax.inject.Inject

class ForgotActivity : BaseActivity(), ISignUpView {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)

    @Inject
    lateinit var presenter: SignupPresenter<ISignUpView>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        presenter.onAttach(this)

        imageView39.setOnClickListener { finish() }
        imageView10.setOnClickListener {if (etEmail.text.toString().trim().isEmpty()){
            toast("Enter email")
        }else{
            if (isInternetConnected()){
                presenter.forgot(etEmail.text.toString().trim())
            }
        }  }


    }

    override fun onSignup(data: SignupModel) {
    }

    override fun onSocialSignup(data: LoginModel) {
        TODO("Not yet implemented")
    }

    override fun onForgot(data: SuccessModel) {
        toast("Forget password mail sent successfully.")
        finish()
    }

    override fun onerror(data: String) {
        toast(data)
    }

    override fun onGeneratedToken(lastAction: String) {
        TODO("Not yet implemented")
    }
}