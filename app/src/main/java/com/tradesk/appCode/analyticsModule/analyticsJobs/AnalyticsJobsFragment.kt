package com.tradesk.appCode.analyticsModule.analyticsJobs

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
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.analyticsModule.analyticsUsers.analyticsUserDetails.JobContractorUsersJobsDetailsAdapter
import com.tradesk.appCode.home.leadsDetail.LeadsActivity
import com.tradesk.base.BaseFragment
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.SearchDrugEntity
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.*
import com.google.android.material.tabs.TabLayout
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_analytics_jobs_details.*
import kotlinx.android.synthetic.main.fragment_analytics_jobs_details.mTvExpenseView
import kotlinx.android.synthetic.main.fragment_analytics_jobs_details.mTvIncomeView

class AnalyticsJobsFragment : BaseFragment(), SingleListCLickListener, IAnalyticsJobsView, SingleItemCLickListener {
    /////////////
    private val TRIGGER_AUTO_COMPLETE: Int = 100
    private val AUTO_COMPLETE_DELAY: Long = 500
    private var handler: Handler? = null

    var mHomeImage = true
    var CheckVersion = true
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }


    //    val mList = mutableListOf<DataShelters>()
    val mHomeLeadsAdapter by lazy { AnalyticsProjectsJobsAdapter(requireActivity(),this) }
    val mJobContractorUsersJobsDetailsAdapter by lazy { JobContractorUsersJobsDetailsAdapter(requireActivity(),this) }

    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity)
            mainActivity = context
    }

    //////////////
    val searchDrugData by lazy { arrayListOf<String>() }


    @Inject
    lateinit var presenter: AnalyticsJobsPresenter<IAnalyticsJobsView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_analytics_jobs_details, container, false)

    override fun setUp(view: View) {
        presenter.onAttach(this)


        val firstTab: TabLayout.Tab = simpleTabLayout.newTab()
        firstTab.text = "Contracts"
        simpleTabLayout.addTab(firstTab)

        val secTab: TabLayout.Tab = simpleTabLayout.newTab()
        secTab.text = "Invoices"
        simpleTabLayout.addTab(secTab)

        val thirdTab: TabLayout.Tab = simpleTabLayout.newTab()
        thirdTab.text = "Receipts"
        simpleTabLayout.addTab(thirdTab)

        mIvBack.setOnClickListener { requireActivity().onBackPressed() }

        mTvIncomeView.setOnClickListener {

            mTvIncomeView.background=resources.getDrawable(R.drawable.appcolor_drawable_dark)
            mTvIncomeView.setTextColor(resources.getColor(R.color.white_color))


            mTvExpenseView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvExpenseView.setTextColor(resources.getColor(R.color.black_color))
        }
        mTvExpenseView.setOnClickListener {

            mTvExpenseView.background=resources.getDrawable(R.drawable.appcolor_drawable_dark)
            mTvExpenseView.setTextColor(resources.getColor(R.color.white_color))


            mTvIncomeView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvIncomeView.setTextColor(resources.getColor(R.color.black_color))
        }
//        val thirdTab: TabLayout.Tab = simpleTabLayout.newTab()
//        thirdTab.text = "Unpaid Invoices"
//        simpleTabLayout.addTab(thirdTab)

        rvProjectsAnalyticsHome.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvProjectsAnalyticsHome.adapter = mHomeLeadsAdapter

        rvNewUsersList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvNewUsersList.adapter = mJobContractorUsersJobsDetailsAdapter
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

    }

    override fun onSingleItemClick(item: Any, position: Int) {
        requireActivity().startActivity(Intent(requireActivity(),LeadsActivity::class.java))
    }


}