package com.tradesk.appCode.splashModule

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import com.tradesk.R
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.loginModule.LoginActivity
import com.tradesk.base.BaseActivity
import com.tradesk.utils.PreferenceConstants

class SplashActivity : BaseActivity() {

    var SPLASH_TIME_OUT = 2000

    val handler by lazy { Handler() }
    val runnable by lazy {
        Runnable {
            if (mPrefs.isUserLoggedIn(PreferenceConstants.USER_LOGGED_IN)) {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        handler.postDelayed(runnable, SPLASH_TIME_OUT.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }


    override fun onGeneratedToken(lastAction: String) {

    }
}