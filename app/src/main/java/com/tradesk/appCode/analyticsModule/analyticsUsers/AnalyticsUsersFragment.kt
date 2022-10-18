package com.tradesk.appCode.analyticsModule.analyticsUsers

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.analyticsModule.MainAnalyticsActivity
import com.tradesk.appCode.analyticsModule.MainAnalyticsActivity.Companion.inner_page_clicked
import com.tradesk.appCode.analyticsModule.analyticsUsers.analyticsUserDetails.AnalyticsUsersDetailFragment
import com.tradesk.appCode.home.salePersonModule.AddSalesPersonActivity
import com.tradesk.base.BaseFragment
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.*
import com.google.android.material.tabs.TabLayout
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_analytics_users.*
import kotlinx.android.synthetic.main.fragment_analytics_users.rvProjectsAnalyticsHome
import kotlinx.android.synthetic.main.fragment_analytics_users.simpleTabLayout

class AnalyticsUsersFragment : BaseFragment(), SingleListCLickListener, IAnalyticsUsersView, SingleItemCLickListener {
    /////////////
    private val TRIGGER_AUTO_COMPLETE: Int = 100
    private val AUTO_COMPLETE_DELAY: Long = 500
    private var handler: Handler? = null

    var tab_click=0
    var mHomeImage = true
    var CheckVersion = true
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }


    val mList = mutableListOf<Client>()
    val mHomeLeadsAdapter by lazy { AnalyticsUsersDetailAdapter(requireActivity(),this,mList) }

    lateinit var mainActivity: MainAnalyticsActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainAnalyticsActivity)
            mainActivity = context
    }

    //////////////
    val searchDrugData by lazy { arrayListOf<String>() }


    @Inject
    lateinit var presenter: AnalyticsUsersPresenter<IAnalyticsUsersView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_analytics_users, container, false)

    override fun setUp(view: View) {
        presenter.onAttach(this)
        val firstTab: TabLayout.Tab = simpleTabLayout.newTab()
        firstTab.text = "All"
        simpleTabLayout.addTab(firstTab)


        val firstSec: TabLayout.Tab = simpleTabLayout.newTab()
        firstSec.text = "Admins"
        simpleTabLayout.addTab(firstSec)


        val firstThird: TabLayout.Tab = simpleTabLayout.newTab()
        firstThird.text = "Managers"
        simpleTabLayout.addTab(firstThird)


        val firstFourth: TabLayout.Tab = simpleTabLayout.newTab()
        firstFourth.text = "Employees"
        simpleTabLayout.addTab(firstFourth)


//        val secTab: TabLayout.Tab = simpleTabLayout.newTab()
//        secTab.text = "Project Manager"
//        simpleTabLayout.addTab(secTab)

        simpleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        tab_click=0
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.userslist("1", "30","")
                        }
                    }
                    1 -> {
                        tab_click=1
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.userslist("1", "30","admin")
                        }
                    }
                    2 -> {
                        tab_click=2
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.userslist("1", "30","manager")
                        }
                    }
                    3 -> {
                        tab_click=3
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.userslist("1", "30","employee")
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        rvProjectsAnalyticsHome.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvProjectsAnalyticsHome.adapter = mHomeLeadsAdapter
        if (isInternetConnected()){
            presenter.userslist("1,","30","")
        }

        mTvAddUsersSales.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    AddSalesPersonActivity::class.java
                )
            )
        }
    }

    override fun onDrugSearched(it: SearchDrugEntity) {
        searchDrugData.clear()

    }


    override fun onGeneratedToken(lastAction: String) {

    }

    override fun enableButton() {

    }

    override fun disableButton() {

    }

    override fun onSingleListClick(item: Any, position: Int) {

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

    override fun onSubUsersResponse(it: ClientsListModel) {
        mList.clear()
        mList.addAll(it.data.client)
        mHomeLeadsAdapter.notifyDataSetChanged()
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
        inner_page_clicked="0"
        if (tab_click==0){
            if (isInternetConnected()) {
                presenter.userslist("1", "30","")
            }
        }else if (tab_click==1){
            if (isInternetConnected()) {
                presenter.userslist("1", "30","admin")
            }
        }else if (tab_click==2){
            if (isInternetConnected()) {
                presenter.userslist("1", "30","manager")
            }
        }else if (tab_click==3){
            if (isInternetConnected()) {
                presenter.userslist("1", "30","employee")
            }
        }

    }

    override fun onSingleItemClick(item: Any, position: Int) {
        inner_page_clicked="1"
        AnalyticsUsersDetailFragment.subuserid=mList[position]._id.toString()
        AnalyticsUsersDetailFragment.subuseremail=mList[position].email.toString()
        AnalyticsUsersDetailFragment.subusername=mList[position].name.toString()
        AnalyticsUsersDetailFragment.subuserimage=mList[position].image.toString()
        AnalyticsUsersDetailFragment.subuserphone=mList[position].phone_no.toString()
        mainActivity.addMainFragment(R.id.mainContainer, AnalyticsUsersDetailFragment())
    }


}