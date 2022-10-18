package com.tradesk.appCode.profileModule.settingsModule

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.tradesk.R
import com.tradesk.appCode.loginModule.LoginActivity
import com.tradesk.appCode.profileModule.settingsModule.changePassword.ChangePasswordActivity
import com.tradesk.base.BaseActivity
import com.tradesk.utils.extension.AllinOneDialog
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mIvBack.setOnClickListener { finish() }
        clChangePassword.setOnClickListener { startActivity(Intent(this,ChangePasswordActivity::class.java).putExtra("type","Change Password")) }
        clTerms.setOnClickListener { startActivity(Intent(this,ChangePasswordActivity::class.java).putExtra("type","Terms $ Conditions")) }
        clPrivacy.setOnClickListener { startActivity(Intent(this,ChangePasswordActivity::class.java).putExtra("type","Privacy Policy")) }
        clLogout.setOnClickListener {

            AllinOneDialog(ttle = "Log out",
            msg = "Are you sure you want to Log Out ?",
            onLeftClick = {/*btn No click*/ },
            onRightClick = {/*btn Yes click*/
                Toast.makeText(
                    this,
                    "Your are successfully logged out.",
                    Toast.LENGTH_SHORT
                ).show()
                mPrefs.logoutUser()

                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    ).putExtra("logout", "true")
                )
                finishAffinity()
                startAnim()
            }
        ) }
    }

    override fun onGeneratedToken(lastAction: String) {

    }
}