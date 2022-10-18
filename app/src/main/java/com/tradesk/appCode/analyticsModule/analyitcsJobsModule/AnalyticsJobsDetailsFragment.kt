package com.tradesk.appCode.analyticsModule.analyitcsJobsModule

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
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.SearchDrugEntity
import com.tradesk.R
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.home.leadsDetail.LeadsActivity
import com.tradesk.base.BaseFragment
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.*
import com.google.android.material.tabs.TabLayout
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_analytics_jobs.*
import kotlinx.android.synthetic.main.fragment_analytics_jobs.simpleTabLayout

class AnalyticsJobsDetailsFragment : BaseFragment(), SingleListCLickListener, IAnalyticsJobsDetailsView, SingleItemCLickListener {
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
    val mHomeLeadsAdapter by lazy { AnalyticsProjectsJobsDetailsAdapter(requireActivity(),this) }
    val mProjectsLeadsAdapter by lazy { AnalyticsProjectsLeadsDetailsAdapter(requireActivity(),this) }

    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity)
            mainActivity = context
    }

    //////////////
    val searchDrugData by lazy { arrayListOf<String>() }


    @Inject
    lateinit var presenter: AnalyticsJobsDetailsPresenter<IAnalyticsJobsDetailsView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_analytics_jobs, container, false)

    override fun setUp(view: View) {
        presenter.onAttach(this)


        val firstTab: TabLayout.Tab = simpleTabLayout.newTab()
        firstTab.text = "Revenue"
        simpleTabLayout.addTab(firstTab)


        val secTab: TabLayout.Tab = simpleTabLayout.newTab()
        secTab.text = "Estimates"
        simpleTabLayout.addTab(secTab)

        val thirdTab: TabLayout.Tab = simpleTabLayout.newTab()
        thirdTab.text = "Invoices"
        simpleTabLayout.addTab(thirdTab)


        val firstTabTwo: TabLayout.Tab = simpleTabLayoutTwo.newTab()
        firstTabTwo.text = "Proposals"
        simpleTabLayoutTwo.addTab(firstTabTwo)


        val secTabTwo: TabLayout.Tab = simpleTabLayoutTwo.newTab()
        secTabTwo.text = "Jobs"
        simpleTabLayoutTwo.addTab(secTabTwo)



        mTvDetailsView.setOnClickListener {
            mTvTitlePage.setText("Income Statistic")
            mainDetailsTwo.visibility=View.GONE
            mainDetailsThree.visibility=View.GONE
            mainDetails.visibility=View.VISIBLE

            mTvDetailsView.background=resources.getDrawable(R.drawable.appcolor_drawable_dark)
            mTvDetailsView.setTextColor(resources.getColor(R.color.white_color))


            mTvLeadsView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvLeadsView.setTextColor(resources.getColor(R.color.black_color))

            mTvProposalsView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvProposalsView.setTextColor(resources.getColor(R.color.black_color))
        }
        mTvLeadsView.setOnClickListener {
            mainDetailsThree.visibility=View.GONE
            mainDetails.visibility=View.GONE
            mainDetailsTwo.visibility=View.VISIBLE
            mTvTitlePage.setText("Leads Detail")
            mTvLeadsView.background=resources.getDrawable(R.drawable.appcolor_drawable_dark)
            mTvLeadsView.setTextColor(resources.getColor(R.color.white_color))


            mTvDetailsView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvDetailsView.setTextColor(resources.getColor(R.color.black_color))

            mTvProposalsView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvProposalsView.setTextColor(resources.getColor(R.color.black_color))
        }


        mTvProposalsView.setOnClickListener {
            mainDetailsTwo.visibility=View.GONE
            mainDetails.visibility=View.GONE
            mainDetailsThree.visibility=View.VISIBLE
            mTvTitlePage.setText("Proposal")
            mTvProposalsView.background=resources.getDrawable(R.drawable.appcolor_drawable_dark)
            mTvProposalsView.setTextColor(resources.getColor(R.color.white_color))


            mTvLeadsView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvLeadsView.setTextColor(resources.getColor(R.color.black_color))

            mTvLeadsView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvLeadsView.setTextColor(resources.getColor(R.color.black_color))
        }


        rvProjectsAnalyticsHome.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvProjectsAnalyticsHome.adapter = mHomeLeadsAdapter

        rvProjectsAnalyticsHomeOne.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvProjectsAnalyticsHomeOne.adapter = mProjectsLeadsAdapter

        rvProjectsAnalyticsHomeTwo.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvProjectsAnalyticsHomeTwo.adapter = mHomeLeadsAdapter

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