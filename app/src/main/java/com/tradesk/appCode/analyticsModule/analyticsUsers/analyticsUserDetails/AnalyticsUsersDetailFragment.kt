package com.tradesk.appCode.analyticsModule.analyticsUsers.analyticsUserDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.analyticsModule.MainAnalyticsActivity
import com.tradesk.appCode.home.leadsDetail.LeadsActivity
import com.tradesk.base.BaseFragment
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.*
import com.google.android.material.tabs.TabLayout
import java.util.*
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_analytics_userdetail.mTvUserDetailsFilter
import kotlinx.android.synthetic.main.fragment_analytics_userdetail.rvProjectsAnalyticsHome
import kotlinx.android.synthetic.main.fragment_analytics_userdetail.simpleTabLayout
import kotlinx.android.synthetic.main.fragment_analytics_userdetailnew.*
import com.tradesk.appCode.home.leadsDetail.CustomerDetailActivity
import com.tradesk.data.entity.*
import java.text.DecimalFormat


class AnalyticsUsersDetailFragment : BaseFragment(), SingleListCLickListener, IAnalyticsUsersDetailView, SingleItemCLickListener {
    /////////////



    private val TRIGGER_AUTO_COMPLETE: Int = 100
    private val AUTO_COMPLETE_DELAY: Long = 500
    private var handler: Handler? = null
    var clickType =0
    var mHomeImage = true
    var CheckVersion = true
    var scrollstart = false
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }


    //    val mList = mutableListOf<DataShelters>()
    val mHomeLeadsAdapter by lazy { AnalyticsUsersDetailAdapter(requireActivity(),this) }
    val mJobContractorUsersJobsDetailsAdapter by lazy { JobContractorUsersJobsDetailsAdapter(requireActivity(),this) }

    lateinit var mainActivity: MainAnalyticsActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainAnalyticsActivity)
            mainActivity = context
    }

    //////////////
    val searchDrugData by lazy { arrayListOf<String>() }


    @Inject
    lateinit var presenter: AnalyticsUsersDetailPresenter<IAnalyticsUsersDetailView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_analytics_userdetailnew, container, false)

    override fun setUp(view: View) {
        presenter.onAttach(this)

        clickType=0
        if (isInternetConnected()){
            presenter.metricjobsmain("job","all",subuserid)
        }

        if (isInternetConnected()){
            presenter.metricjobslist(subuserid,"all","1","20","job")
        }

        mTvDetailsView.setOnClickListener {
            mTvTitleSectionOne.setText("Total Jobs")
            mTvTitleSectionTwo.setText("Pending Jobs")
            mTvTitleSectionThree.setText("Ongoing Jobs")
            mTvTitleSectionFour.setText("Completed Jobs")
//            mTvTitleSectionBelowRecyclerView.setText("Projects")
//            mTvTitleSectionTwoBelowRecyclerView.setText("Project Name")
//            mTvTitleSectionThreeBelowRecyclerView.setText("Description")
//            mTvJobsFilter.setText("All")
            clickType=0

            mTvDetailsView.background=resources.getDrawable(R.drawable.appcolor_drawable_dark)

            mIvOne.setImageDrawable(resources.getDrawable(R.drawable.ic_totaljobsnewicon))
            mIvTwo.setImageDrawable(resources.getDrawable(R.drawable.ic_reloadred))
            mIvThree.setImageDrawable(resources.getDrawable(R.drawable.ic_bluedrops))
            mIvFour.setImageDrawable(resources.getDrawable(R.drawable.ic_tick_squaregreen))

            mTvDetailsView.setTextColor(resources.getColor(R.color.white_color))


            mTvLeadsView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvLeadsView.setTextColor(resources.getColor(R.color.black_color))

            mTvProposalsView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvProposalsView.setTextColor(resources.getColor(R.color.black_color))

            if (isInternetConnected()){
                presenter.metricjobsmain("job","all",subuserid)
            }

            if (isInternetConnected()){
                presenter.metricjobslist(subuserid,"all","1","20","job")
            }
        }
        mTvLeadsView.setOnClickListener {
            mTvTitleSectionOne.setText("Total Leads")
            mTvTitleSectionTwo.setText("Winning Leads")
            mTvTitleSectionThree.setText("Ongoing Leads")
            mTvTitleSectionFour.setText("Follow Ups")
//            mTvTitleSectionBelowRecyclerView.setText("Leads")

            mIvOne.setImageDrawable(resources.getDrawable(R.drawable.ic_totalleadsblue))
            mIvTwo.setImageDrawable(resources.getDrawable(R.drawable.ic_winningiconnew))
            mIvThree.setImageDrawable(resources.getDrawable(R.drawable.ic_bluedrops))
            mIvFour.setImageDrawable(resources.getDrawable(R.drawable.ic_followupnewicon))

//            mTvTitleSectionTwoBelowRecyclerView.setText("Job Title")
//            mTvTitleSectionThreeBelowRecyclerView.setText("Client")
//
//            mTvJobsFilter.setText("All")

            clickType=1
            mTvLeadsView.background=resources.getDrawable(R.drawable.appcolor_drawable_dark)
            mTvLeadsView.setTextColor(resources.getColor(R.color.white_color))


            mTvDetailsView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvDetailsView.setTextColor(resources.getColor(R.color.black_color))

            mTvProposalsView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvProposalsView.setTextColor(resources.getColor(R.color.black_color))

            if (isInternetConnected()){
                presenter.metricjobsmain("lead","all",subuserid)
            }

            if (isInternetConnected()){
                presenter.metricjobslist(subuserid,"all","1","20","lead")
            }
        }
        mTvProposalsView.setOnClickListener {
            mTvTitleSectionOne.setText("Total Proposals")
            mTvTitleSectionTwo.setText("Converted Jobs")
            mTvTitleSectionThree.setText("Sent to Revenue")
            mTvTitleSectionFour.setText("Converted Revenue")
//            mTvTitleSectionBelowRecyclerView.setText("Proposals")

            mIvOne.setImageDrawable(resources.getDrawable(R.drawable.ic_totlaproposalsblue))
            mIvTwo.setImageDrawable(resources.getDrawable(R.drawable.ic_convertedwork))
            mIvThree.setImageDrawable(resources.getDrawable(R.drawable.ic_sendrevenuenewicon))
            mIvFour.setImageDrawable(resources.getDrawable(R.drawable.ic_convertedrevenue))

//            mTvTitleSectionTwoBelowRecyclerView.setText("Proposal Title")
//            mTvTitleSectionThreeBelowRecyclerView.setText("Client")
//            mTvJobsFilter.setText("All")
            clickType=2

            mTvProposalsView.background=resources.getDrawable(R.drawable.appcolor_drawable_dark)
            mTvProposalsView.setTextColor(resources.getColor(R.color.white_color))


            mTvLeadsView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvLeadsView.setTextColor(resources.getColor(R.color.black_color))

            mTvDetailsView.background=resources.getDrawable(R.drawable.white_drawable_dark)
            mTvDetailsView.setTextColor(resources.getColor(R.color.black_color))

            if (isInternetConnected()) {
                presenter.userproposallist(subuserid, "all", "1", "30", "proposal")
            }
        }

        val firstTab: TabLayout.Tab = simpleTabLayout.newTab()
        firstTab.text = "Leads"
        simpleTabLayout.addTab(firstTab)

        val secTab: TabLayout.Tab = simpleTabLayout.newTab()
        secTab.text = "Jobs"
        simpleTabLayout.addTab(secTab)

        val thirdTab: TabLayout.Tab = simpleTabLayout.newTab()
        thirdTab.text = "Proposals"
        simpleTabLayout.addTab(thirdTab)

        mTvName.setText(subusername)
        mTvPhone.setText(subuserphone)
        mTvEmail.setText(subuseremail)
        if (subuserimage.isNotEmpty()){
            mCvProfile.loadWallImage(subuserimage)
        }
        mTvUserDetailsFilter.setOnClickListener { showLogoutMenu(mTvUserDetailsFilter,1) }

        rvProjectsAnalyticsHome.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvProjectsAnalyticsHome.adapter = mHomeLeadsAdapter



    }


    override fun onResponse(it: ParticularUserModel) {
        val formatter = DecimalFormat("#,###,###")
        mTvTotalJobs.setText(formatter.format(it.data.totalProposal).toString())
        mTvPendingJobs.setText(formatter.format(it.data.pendingProposal).toString())
        mTvOngoingJobs.setText(formatter.format(it.data.inprocessProposal).toString())
        mTvCompletedJobs.setText(formatter.format(it.data.completedProposal).toString())



    }

    override fun onRevenueResponse(it: ReveneModel) {
        val formatter = DecimalFormat("#,###,###")
        textView70.setText(formatter.format(it.data.jobsData.Sales).toString())
        textView73.setText(formatter.format(it.data.jobsData.Revenue).toString())
        textView88.setText(formatter.format(it.data.jobsData.total_expenses).toString())
        textView98.setText(formatter.format(it.data.jobsData.NetIncome).toString())
    }

    override fun onListResponse(it: SignleUserJobsModel) {

        val formatter = DecimalFormat("#,###,###")

        if (clickType==0){
            mTvTotalJobs.setText(formatter.format(it.data.totalJobs).toString())
            mTvPendingJobs.setText(formatter.format(it.data.pendingJobs).toString())
            mTvOngoingJobs.setText(formatter.format(it.data.ongoingJobs).toString())
            mTvCompletedJobs.setText(formatter.format(it.data.completedJobs).toString())
        }else{
            mTvTotalJobs.setText(formatter.format(it.data.totalJobs).toString())
            mTvPendingJobs.setText(formatter.format(it.data.completedJobs).toString())
            mTvOngoingJobs.setText(formatter.format(it.data.ongoingJobs).toString())
            mTvCompletedJobs.setText(formatter.format(it.data.followUpJobs).toString())
        }




    }


    override fun onGeneratedToken(lastAction: String) {

    }

    override fun enableButton() {

    }

    override fun disableButton() {

    }

    override fun onSingleListClick(item: Any, position: Int) {


    }


    override fun onResume() {
        super.onResume()

    }

    override fun onSingleItemClick(item: Any, position: Int) {
        if (item.toString().equals("123")){
            requireActivity().startActivity(Intent(requireActivity(), CustomerDetailActivity::class.java))
        }else {
            requireActivity().startActivity(Intent(requireActivity(), LeadsActivity::class.java))
        }
    }

    fun showLogoutMenu(anchor: View, position: Int ): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.dates_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{
            mTvUserDetailsFilter.setText(it.title)
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }


    companion object {
        var subuserid = ""
        var subusername = ""
        var subuserimage = ""
        var subuserphone = ""
        var subuseremail = ""

    }

}