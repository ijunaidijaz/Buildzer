package com.tradesk.appCode.analyticsModule

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.tradesk.R
import com.tradesk.appCode.analyticsModule.analyticsHome.AnalyticsHomeFragment
import com.tradesk.appCode.analyticsModule.analyticsIncomeExpense.AnalyticsIncomeExpenseFragment
import com.tradesk.appCode.analyticsModule.analyticsTimesheet.AnalyticsTimeSheetFragment
import com.tradesk.appCode.analyticsModule.analyticsUsers.AnalyticsUsersFragment
import com.tradesk.base.BaseActivity
import com.tradesk.utils.CommonUtil
import com.tradesk.utils.GlobalVariable
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.changeMainFragment
import com.tradesk.utils.extension.loadWallImage
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main_analytics.*
import javax.inject.Inject

class MainAnalyticsActivity : BaseActivity(), HasSupportFragmentInjector {

    companion object {
        var canClose = true
        var inner_page_clicked="0"
    }

    val homeFragment: AnalyticsHomeFragment by lazy { AnalyticsHomeFragment() }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_analytics)


        if (permissionFile.checklocationPermissions(this)) {
            startLocationUpdates()
        }

        if (mPrefs.getKeyValue(PreferenceConstants.USER_LOGO).isNotEmpty()){
            imageView56.loadWallImage(mPrefs.getKeyValue(PreferenceConstants.USER_LOGO))
        }
        if (mPrefs.getKeyValue(PreferenceConstants.LANGUAGE).isEmpty()) {
            mPrefs.setKeyValue(PreferenceConstants.LANGUAGE, "en")
        }
        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")){
            bottomNavigationView.visibility=View.VISIBLE
            bottomNavigationViewSales.visibility=View.GONE
        }else{
            bottomNavigationView.visibility=View.GONE
            bottomNavigationViewSales.visibility=View.VISIBLE
        }

        imageView56.setOnClickListener { finish() }
        changeMainFragment(mainContainer, homeFragment)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            if (SystemClock.elapsedRealtime() - lastclicked > 500) {
//                if (it.itemId == bottomNavigationView.selectedItemId) return@setOnNavigationItemSelectedListener false
                when (it.itemId) {
                    R.id.menu_home -> {
                        mTvTitle.text="Reports"
                        inner_page_clicked="0"
                        changeMainFragment(mainContainer, homeFragment)
                    }
//                    R.id.menu_job -> {
////                        val args = Bundle()
////                        args.putString("from_dashboard", "no")
////                        val charityFragment = CharityFragment()
////                        charityFragment.setArguments(args)
//                        changeMainFragment(mainContainer, AnalyticsJobsDetailsFragment())
//                    }
                    R.id.menu_time -> {
                        inner_page_clicked="0"
                        mTvTitle.text="My Timesheet"
                        changeMainFragment(mainContainer, AnalyticsTimeSheetFragment())
                    }

                    R.id.menu_revenue -> {
                        inner_page_clicked="0"
                        mTvTitle.text="Revenue"
                        changeMainFragment(mainContainer, AnalyticsIncomeExpenseFragment())
                    }

                    R.id.menu_users -> {
                        mTvTitle.text="Users"
                        inner_page_clicked="0"
                        changeMainFragment(mainContainer, AnalyticsUsersFragment())
                    }
                }
                lastclicked = SystemClock.elapsedRealtime()
                true
            } else {
                false
            }
        }

        bottomNavigationViewSales.setOnNavigationItemSelectedListener {
            if (SystemClock.elapsedRealtime() - lastclicked > 500) {
//                if (it.itemId == bottomNavigationView.selectedItemId) return@setOnNavigationItemSelectedListener false
                when (it.itemId) {
                    R.id.menu_home -> {
                        mTvTitle.text="Reports"
                        inner_page_clicked="0"
                        changeMainFragment(mainContainer, homeFragment)
                    }
//                    R.id.menu_job -> {
////                        val args = Bundle()
////                        args.putString("from_dashboard", "no")
////                        val charityFragment = CharityFragment()
////                        charityFragment.setArguments(args)
//                        changeMainFragment(mainContainer, AnalyticsJobsDetailsFragment())
//                    }
                    R.id.menu_time -> {
                        inner_page_clicked="0"
                        mTvTitle.text="My Timesheet"
                        changeMainFragment(mainContainer, AnalyticsTimeSheetFragment())
                    }

                    R.id.menu_revenue -> {
                        inner_page_clicked="0"
                        mTvTitle.text="Revenue"
                        changeMainFragment(mainContainer, AnalyticsIncomeExpenseFragment())
                    }

                    R.id.menu_users -> {
                        mTvTitle.text="Users"
                        inner_page_clicked="0"
                        changeMainFragment(mainContainer, AnalyticsUsersFragment())
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
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) CommonUtil.permissionAlert(
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