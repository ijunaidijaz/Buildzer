package com.tradesk.appCode

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.tradesk.R
import com.tradesk.appCode.calendarModule.CalendarFragment
import com.tradesk.appCode.home.HomeFragment
import com.tradesk.appCode.jobsModule.JobsFragment
import com.tradesk.appCode.notificationsModule.NotificationFragment
import com.tradesk.appCode.profileModule.ProfileFragment
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.ProfileModel
import com.tradesk.utils.CommonUtil.permissionAlert
import com.tradesk.utils.GlobalVariable
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.changeMainFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity(), HasSupportFragmentInjector {
    companion object {
        var canClose = true
        var page_clicked = "0"
        lateinit var context: MainActivity
    }


    val homeFragment: HomeFragment by lazy { HomeFragment() }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)
        context = this;

        if (permissionFile.checklocationPermissions(this)) {
            startLocationUpdates()
        }

        if (mPrefs.getKeyValue(PreferenceConstants.LANGUAGE).isEmpty()) {
            mPrefs.setKeyValue(PreferenceConstants.LANGUAGE, "en")
        }
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("notification")) {
                // extract the extra-data in the Notification
                changeMainFragment(mainContainer, NotificationFragment())
                bottomNavigationView.selectedItemId = R.id.menu_notification;
            } else changeMainFragment(mainContainer, homeFragment)
        } else {
            changeMainFragment(mainContainer, homeFragment)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            if (SystemClock.elapsedRealtime() - lastclicked > 500) {
//                if (it.itemId == bottomNavigationView.selectedItemId) return@setOnNavigationItemSelectedListener false
                when (it.itemId) {
                    R.id.menu_home -> {
                        page_clicked = "1"
                        changeMainFragment(mainContainer, homeFragment)
                    }
                    R.id.menu_jobs -> {
                        page_clicked = "2"
//                        val args = Bundle()
//                        args.putString("from_dashboard", "no")
//                        val charityFragment = CharityFragment()
//                        charityFragment.setArguments(args)
                        changeMainFragment(mainContainer, JobsFragment())
                    }
                    R.id.menu_calendar -> {
                        page_clicked = "3"
                        changeMainFragment(mainContainer, CalendarFragment())
                    }

                    R.id.menu_notification -> {
                        page_clicked = "4"
                        changeMainFragment(mainContainer, NotificationFragment())
                    }

                    R.id.menu_profile -> {
                        page_clicked = "5"
                        changeMainFragment(mainContainer, ProfileFragment())
                    }
                }
                lastclicked = SystemClock.elapsedRealtime()
                true
            } else {
                false
            }
        }

    }

    var lastclicked = 0L
    override fun onStart() {
        super.onStart()

    }

    @Inject
    internal lateinit var fragmentInjectr: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjectr
    }

    override fun onGeneratedToken(lastAction: String) {

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == GlobalVariable.REQUEST_CODE_LOCATION && permissions.size > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) permissionAlert(
                this, getString(
                    R.string.location_permission_dialog
                )
            )
            else startLocationUpdates()
        }
    }

//    fun addNewFragment(frag:Fragment){
//        supportFragmentManager.beginTransaction().addToBackStack(frag.tag)
//            .add(R.id.homeContainer, frag,frag.tag)
//            .commit()
//    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStackImmediate()
        else
            super.onBackPressed()
//        if (canClose) {
//            finishAffinity()
//        } else {
//            bottomNavigationView.menu.children.forEach {
//                when (it.itemId) {
//                    R.id.menu_home -> changeMainFragment(homeContainer, homeFragment)
//                    R.id.menu_charities -> changeMainFragment(homeContainer, HomeFragment())
//                    R.id.menu_settings -> changeMainFragment(homeContainer, HomeFragment())
//                    else -> changeMainFragment(homeContainer, HomeFragment())
//                }
//            }
//        }
    }


}
