package com.tradesk.appCode.jobsModule

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.home.addJobsModule.AddJobActivity
import com.tradesk.appCode.jobsModule.jobDetailModule.JobDetailActivity
import com.tradesk.appCode.profileModule.settingsModule.SettingsActivity
import com.tradesk.base.BaseFragment
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.*
import java.util.*
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_jobs.*

import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_jobs.mEtSearchName
import kotlinx.android.synthetic.main.fragment_jobs.simpleTabLayout


class JobsFragment : BaseFragment(), SingleListCLickListener, IJobsView {
    /////////////
    private val TRIGGER_AUTO_COMPLETE: Int = 100
    private val AUTO_COMPLETE_DELAY: Long = 500
    private var handler: Handler? = null
    var clicked = ""
    var mHomeImage = true
    var CheckVersion = true
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }


    val mList = mutableListOf<LeadsData>()
    val mJobsAdapter by lazy { JobsAdapter(requireActivity(), this, mList,mList) }

    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity)
            mainActivity = context
    }

    //////////////
    val searchDrugData by lazy { arrayListOf<String>() }


    @Inject
    lateinit var presenter: JobsPresenter<IJobsView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_jobs, container, false)

    override fun setUp(view: View) {
        presenter.onAttach(this)

        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")) {
            mIvAddJobs.visibility = View.VISIBLE
        } else {
            mIvAddJobs.visibility = View.GONE
        }

        val firstTab: TabLayout.Tab = simpleTabLayout.newTab()
        firstTab.text = "All Jobs"
        simpleTabLayout.addTab(firstTab)


        val secTab: TabLayout.Tab = simpleTabLayout.newTab()
        secTab.text = "Pending"
        simpleTabLayout.addTab(secTab)

        val thirdTab: TabLayout.Tab = simpleTabLayout.newTab()
        thirdTab.text = "Ongoing"
        simpleTabLayout.addTab(thirdTab)

        val forthTab: TabLayout.Tab = simpleTabLayout.newTab()
        forthTab.text = "Complete"
        simpleTabLayout.addTab(forthTab)
        mEtSearchName.addWatcher {
            mJobsAdapter.filter.filter(it)
        }
        simpleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        clicked = "0x"
                        mList.clear()
                        mJobsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.getjobs("1", "30", "all")
                        }
                    }
                    1 -> {
                        clicked = "1"
                        mList.clear()
                        mJobsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.getjobs("1", "30", "pending")
                        }
                    }
                    2 -> {
                        clicked = "2"
                        mList.clear()
                        mJobsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.getjobs("1", "30", "ongoing")
                        }
                    }
                    3 -> {
                        clicked = "3"
                        mList.clear()
                        mJobsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.getjobs("1", "30", "completed")
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        mIvAddJob.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity(),
                    SettingsActivity::class.java
                )
            )
        }
        mIvAddJobs.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    AddJobActivity::class.java
                )
            )
        }

        mTvDateFilter.setOnClickListener { showLogoutMenu(mTvDateFilter, 1) }
        rvJobs.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvJobs.adapter = mJobsAdapter

        if (isInternetConnected()) {
            presenter.getjobs("1", "30", "all")
        }
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun enableButton() {

    }

    override fun disableButton() {

    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (item.equals("2")) {
            val uri = java.lang.String.format(
                Locale.ENGLISH,
                "geo:%f,%f",
                mList[position].address.location.coordinates[0],
                mList[position].address.location.coordinates[1]
            )
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        } else if (item.equals("3")) {
//            val emailIntent = Intent(
//                Intent.ACTION_SENDTO, Uri.fromParts(
//                    "mailto", mList[position].client[0].email, null
//                )
//            )
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Related to lead")
//            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi "+mList[position].client[0].name)
//            startActivity(Intent.createChooser(emailIntent, "Send email..."))

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("smsto:" + "+1 " + mList[position].client[0].phone_no)
            intent.putExtra("sms_body", "")
            startActivity(intent)
        } else if (item.equals("4")) {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "+1 " + mList[position].client[0].phone_no)
            startActivity(dialIntent)
        } else {
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    JobDetailActivity::class.java
                ).putExtra("id", mList.get(position)._id)
            )
        }
    }

    override fun onJobsList(it: LeadsModel) {
        mList.clear()
        mList.addAll(it.data.leadsData)
        mJobsAdapter.notifyDataSetChanged()
    }

    override fun onAppVersionResp(it: AppVersionEntity) {
//        it.data.version="2"
        CheckVersion = false
        try {
            if (checkVersionCode() < it.data.version.toDouble() && checkVersionCode() > 0) {
                AllinOneDialog(getString(R.string.app_name),
                    "New version of givebackRx is available on store. Please update your app with latest version.",
                    btnLeft = "Cancel",
                    btnRight = "Update",
                    onLeftClick = {},
                    onRightClick = {
                        if (isInternetConnected()) {
                            val i = Intent(android.content.Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.buildzer"));
                            startActivity(i);
                        }
                    })
            }
        } catch (e: Exception) {

        }
    }

    private fun checkVersionCode(): Float {
        try {
            val pInfo = requireActivity().getPackageManager()
                .getPackageInfo(requireActivity().packageName, 0)
            return pInfo.versionCode.toFloat()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0.0f
    }

    override fun onResume() {
        super.onResume()
        if (clicked.equals("0")) {
            mList.clear()
            mJobsAdapter.notifyDataSetChanged()
            if (isInternetConnected()) {
                presenter.getjobs("1", "30", "all")
            }
        } else if (clicked.equals("1")) {
            mList.clear()
            mJobsAdapter.notifyDataSetChanged()
            if (isInternetConnected()) {
                presenter.getjobs("1", "30", "pending")
            }
        } else if (clicked.equals("2")) {
            mList.clear()
            mJobsAdapter.notifyDataSetChanged()
            if (isInternetConnected()) {
                presenter.getjobs("1", "30", "ongoing")
            }
        } else if (clicked.equals("3")) {
            mList.clear()
            mJobsAdapter.notifyDataSetChanged()
            if (isInternetConnected()) {
                presenter.getjobs("1", "30", "completed")
            }
        } else {
            mList.clear()
            mJobsAdapter.notifyDataSetChanged()
            if (isInternetConnected()) {
                presenter.getjobs("1", "30", "all")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clicked = "0"
    }

    fun showLogoutMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.dates_menu, popup.getMenu())
        popup.setOnMenuItemClickListener {
            mTvDateFilter.setText(it.title)
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }


}