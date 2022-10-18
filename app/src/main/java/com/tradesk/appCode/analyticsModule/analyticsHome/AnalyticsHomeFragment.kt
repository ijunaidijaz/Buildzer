package com.tradesk.appCode.analyticsModule.analyticsHome

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
import com.tradesk.base.BaseFragment

import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.*
import java.util.*
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_analytics_home.*
import com.tradesk.appCode.analyticsModule.MainAnalyticsActivity
import com.tradesk.appCode.analyticsModule.analyticsHome.viewDetails.ViewDetailsActivity
import com.tradesk.appCode.analyticsModule.analyticsJobs.AnalyticsJobsFragment
import com.tradesk.appCode.profileModule.proposalsModule.ProposalsAdapter
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.PDFViewNewActivity
import com.tradesk.data.entity.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_analytics_home.constraintLayout14
import kotlinx.android.synthetic.main.fragment_analytics_home.mIvFour
import kotlinx.android.synthetic.main.fragment_analytics_home.mIvOne
import kotlinx.android.synthetic.main.fragment_analytics_home.mIvThree
import kotlinx.android.synthetic.main.fragment_analytics_home.mIvTwo
import kotlinx.android.synthetic.main.fragment_analytics_home.mTvDetailsView
import kotlinx.android.synthetic.main.fragment_analytics_home.mTvLeadsView
import kotlinx.android.synthetic.main.fragment_analytics_home.mTvProposalsView
import kotlinx.android.synthetic.main.fragment_analytics_home.mTvTitleSectionFour
import kotlinx.android.synthetic.main.fragment_analytics_home.mTvTitleSectionOne
import kotlinx.android.synthetic.main.fragment_analytics_home.mTvTitleSectionThree
import kotlinx.android.synthetic.main.fragment_analytics_home.mTvTitleSectionTwo
import kotlinx.android.synthetic.main.fragment_analytics_home.mTvViewFour
import kotlinx.android.synthetic.main.fragment_analytics_home.mTvViewOne
import kotlinx.android.synthetic.main.fragment_analytics_home.mTvViewThree
import kotlinx.android.synthetic.main.fragment_analytics_home.mTvViewTwo
import kotlinx.android.synthetic.main.fragment_analytics_home.rvProjectsAnalyticsHome
import kotlinx.android.synthetic.main.fragment_analytics_home.simpleTabLayout
import java.text.DecimalFormat

class AnalyticsHomeFragment : BaseFragment(), SingleListCLickListener, IAnalyticsHomeView,
    SingleItemCLickListener {
    /////////////
    private val TRIGGER_AUTO_COMPLETE: Int = 100
    private val AUTO_COMPLETE_DELAY: Long = 500
    private var handler: Handler? = null
    var selected_position = 0
    var clickType = 1
    var mHomeImage = true
    var CheckVersion = true
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }

    val mList = mutableListOf<LeadsData>()
    val mHomeLeadsAdapter by lazy { AnalyticsProjectsHomeAdapter(requireActivity(), this, mList) }

    val mListProposals = mutableListOf<Proposal>()
    val mProposalsAdapter by lazy { ProposalsAdapter(requireActivity(), mListProposals,mListProposals, this) }

    lateinit var mainActivity: MainAnalyticsActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainAnalyticsActivity)
            mainActivity = context
    }

    //////////////
    val searchDrugData by lazy { arrayListOf<String>() }


    @Inject
    lateinit var presenter: AnalyticsHomePresenter<IAnalyticsHomeView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(com.tradesk.R.layout.fragment_analytics_home, container, false)

    override fun setUp(view: View) {
        presenter.onAttach(this)

        rvProjectsAnalyticsHome.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvProjectsAnalyticsHome.adapter = mHomeLeadsAdapter


        rvProposalsAna.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvProposalsAna.adapter = mProposalsAdapter


        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")) {
            if (isInternetConnected()) {
                presenter.getLeads("1", "30", "all")
            }

            if (isInternetConnected()) {
                presenter.metricjobsmain("lead", "all")
            }
            mTvProposalsView.visibility=View.VISIBLE
        }else{
            if (isInternetConnected()){
            presenter.metricjobsmain("lead","all", mPrefs.getKeyValue(PreferenceConstants.USER_ID))
            }

           if (isInternetConnected()){
            presenter.metricjobslist(mPrefs.getKeyValue(PreferenceConstants.USER_ID),"all","1","20","true","lead")
           }
            mTvProposalsView.visibility=View.GONE
        }

        mTvDetailsView.setOnClickListener {

            mTvTitleSectionOne.setText("Total Leads")
            mTvTitleSectionTwo.setText("Pending Leads")
            mTvTitleSectionThree.setText("Follow Up")
            mTvTitleSectionFour.setText("Sale")
            mTvTitleSectionBelowRecyclerView.setText("Leads")

            mIvOne.setImageDrawable(resources.getDrawable(R.drawable.ic_totalleadsblue))
            mIvTwo.setImageDrawable(resources.getDrawable(R.drawable.ic_followupnewicon))
            mIvThree.setImageDrawable(resources.getDrawable(R.drawable.ic_bluedrops))
            mIvFour.setImageDrawable(resources.getDrawable(R.drawable.ic_winningiconnew))

            mTvTitleSectionTwoBelowRecyclerView.setText("Job Title")
            mTvTitleSectionThreeBelowRecyclerView.setText("Client")

            mTvJobsFilter.setText("All")

            clickType = 1
            mTvLeadsView.background = resources.getDrawable(R.drawable.white_drawable_dark)
            mTvLeadsView.setTextColor(resources.getColor(R.color.black_color))


            mTvDetailsView.background = resources.getDrawable(R.drawable.appcolor_drawable_dark)
            mTvDetailsView.setTextColor(resources.getColor(R.color.white_color))

            mTvProposalsView.background = resources.getDrawable(R.drawable.white_drawable_dark)
            mTvProposalsView.setTextColor(resources.getColor(R.color.black_color))

//            if (isInternetConnected()) {
//                presenter.getLeads("1", "30", "all")
//            }
//            if (isInternetConnected()) {
//                presenter.metricjobsmain("lead",  "all")
//            }



            if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")) {
                if (isInternetConnected()) {
                    presenter.getLeads("1", "30", "all")
                }

                if (isInternetConnected()) {
                    presenter.metricjobsmain("lead", "all")
                }
                mTvProposalsView.visibility=View.VISIBLE
            }else{
                if (isInternetConnected()){
                    presenter.metricjobsmain("lead","all", mPrefs.getKeyValue(PreferenceConstants.USER_ID))
                }

                if (isInternetConnected()){
                    presenter.metricjobslist(mPrefs.getKeyValue(PreferenceConstants.USER_ID),"all","1","20","true","lead")
                }
                mTvProposalsView.visibility=View.GONE
            }

        }
        mTvLeadsView.setOnClickListener {
          mTvTitleSectionOne.setText("Total Jobs")
            mTvTitleSectionTwo.setText("Pending Jobs")
            mTvTitleSectionThree.setText("Ongoing Jobs")
            mTvTitleSectionFour.setText("Complete Jobs")
            mTvTitleSectionBelowRecyclerView.setText("Projects")
            mTvTitleSectionTwoBelowRecyclerView.setText("Project Name")
            mTvTitleSectionThreeBelowRecyclerView.setText("Description")
            mTvJobsFilter.setText("All")
            clickType = 0



            mIvOne.setImageDrawable(resources.getDrawable(R.drawable.ic_totaljobsnewicon))
            mIvTwo.setImageDrawable(resources.getDrawable(R.drawable.ic_reloadred))
            mIvThree.setImageDrawable(resources.getDrawable(R.drawable.ic_bluedrops))
            mIvFour.setImageDrawable(resources.getDrawable(R.drawable.ic_tick_squaregreen))

            mTvDetailsView.background = resources.getDrawable(R.drawable.white_drawable_dark)
            mTvDetailsView.setTextColor(resources.getColor(R.color.black_color))


            mTvLeadsView.background = resources.getDrawable(R.drawable.appcolor_drawable_dark)
            mTvLeadsView.setTextColor(resources.getColor(R.color.white_color))

            mTvProposalsView.background = resources.getDrawable(R.drawable.white_drawable_dark)
            mTvProposalsView.setTextColor(resources.getColor(R.color.black_color))

//            if (isInternetConnected()) {
//                presenter.getjobs("1", "30", "all")
//            }
//
//            if (isInternetConnected()) {
//                presenter.metricjobsmain("job",  "all")
//            }

            if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")) {
                if (isInternetConnected()) {
                    presenter.getLeads("1", "30", "all")
                }

                if (isInternetConnected()) {
                    presenter.metricjobsmain("job", "all")
                }
                mTvProposalsView.visibility=View.VISIBLE
            }else{
                if (isInternetConnected()){
                    presenter.metricjobsmain("job","all", mPrefs.getKeyValue(PreferenceConstants.USER_ID))
                }

                if (isInternetConnected()){
                    presenter.metricjobslist(mPrefs.getKeyValue(PreferenceConstants.USER_ID),"all","1","20","","job")
                }
                mTvProposalsView.visibility=View.GONE
            }
        }
        mTvProposalsView.setOnClickListener {
            mTvTitleSectionOne.setText("Total Proposals")
            mTvTitleSectionTwo.setText("Potential Revenue")
            mTvTitleSectionThree.setText("Converted Jobs")
            mTvTitleSectionFour.setText("Converted Revenue")
            mTvTitleSectionBelowRecyclerView.setText("Proposals")

            mIvOne.setImageDrawable(resources.getDrawable(R.drawable.ic_totlaproposalsblue))
            mIvTwo.setImageDrawable(resources.getDrawable(R.drawable.ic_convertedwork))
            mIvThree.setImageDrawable(resources.getDrawable(R.drawable.ic_convertedrevenue))
            mIvFour.setImageDrawable(resources.getDrawable(R.drawable.ic_sendrevenuenewicon))

            mTvTitleSectionTwoBelowRecyclerView.setText("Proposal")
            mTvTitleSectionThreeBelowRecyclerView.setText("")
            mTvJobsFilter.setText("All")
            clickType = 2

            mTvProposalsView.background = resources.getDrawable(R.drawable.appcolor_drawable_dark)
            mTvProposalsView.setTextColor(resources.getColor(R.color.white_color))


            mTvLeadsView.background = resources.getDrawable(R.drawable.white_drawable_dark)
            mTvLeadsView.setTextColor(resources.getColor(R.color.black_color))

            mTvDetailsView.background = resources.getDrawable(R.drawable.white_drawable_dark)
            mTvDetailsView.setTextColor(resources.getColor(R.color.black_color))

            if (isInternetConnected()) {
                presenter.getProposalRevenue("proposal", "")
                presenter.getProposals("1", "30", "", "proposal", "")
            }
        }


        val firstTab: TabLayout.Tab = simpleTabLayout.newTab()
        firstTab.text = "All"
        simpleTabLayout.addTab(firstTab)


        val firstSec: TabLayout.Tab = simpleTabLayout.newTab()
        firstSec.text = "Pending"
        simpleTabLayout.addTab(firstSec)


        val firstThird: TabLayout.Tab = simpleTabLayout.newTab()
        firstThird.text = "Ongoing"
        simpleTabLayout.addTab(firstThird)

        val firstFourth: TabLayout.Tab = simpleTabLayout.newTab()
        firstFourth.text = "Complete"
        simpleTabLayout.addTab(firstFourth)

        simpleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        mList.clear()
                        mListProposals.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        mProposalsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            if (clickType == 0) {
                                presenter.getjobs("1", "30", "all")
                            } else if (clickType == 1) {
                                presenter.getLeads("1", "30", "all")
                            } else if (clickType == 2) {
//                                presenter.getProposalRevenue("proposal", "")
                                presenter.getProposals("1", "30", "", "proposal", "")
                            }

                        }
                    }
                    1 -> {
                        mList.clear()
                        mListProposals.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            if (clickType == 0) {
                                presenter.getjobs("1", "30", "ongoing")
                            } else if (clickType == 1) {
                                presenter.getLeads("1", "30", "follow_up")
                            } else if (clickType == 2) {
//                                presenter.getProposalRevenue("proposal", "")
                                presenter.getProposals("1", "30", "pending", "proposal", "")
                            }
                        }

                    }
                    2 -> {
                        mListProposals.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            if (clickType == 0) {
                                presenter.getjobs("1", "30", "pending")
                            } else if (clickType == 1) {
                                presenter.getLeads("1", "30", "pending")
                            } else if (clickType == 2) {
//                                presenter.getProposalRevenue("proposal", "")
                                presenter.getProposals("1", "30", "Inprocess", "proposal", "")
                            }

                        }
                    }
                    3 -> {
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        mListProposals.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            if (clickType == 0) {
                                presenter.getjobs("1", "30", "completed")
                            } else if (clickType == 1) {
                                presenter.getLeads("1", "30", "sale")
                            } else if (clickType == 2) {
//                                presenter.getProposalRevenue("proposal", "")
                                presenter.getProposals("1", "30", "completed", "proposal", "")
                            }
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


        mTvTopFilter.setOnClickListener { showLogoutMenu(mTvTopFilter, 1) }
        mTvViewOne.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    ViewDetailsActivity::class.java
                ).putExtra("title", "First").putExtra("clickType", clickType.toString())
            )
        }
        mTvViewTwo.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    ViewDetailsActivity::class.java
                ).putExtra("title", "Second").putExtra("clickType", clickType.toString())
            )
        }
        mTvViewThree.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    ViewDetailsActivity::class.java
                ).putExtra("title", "Third").putExtra("clickType", clickType.toString())
            )
        }
        mTvViewFour.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    ViewDetailsActivity::class.java
                ).putExtra("title", "Fourth").putExtra("clickType", clickType.toString())
            )
        }
        mTvJobsFilter.setOnClickListener {
            if (clickType == 0) {
                showJobsFilterMenu(mTvJobsFilter, 1)
            } else if (clickType == 1) {
                showLeadsFilterMenu(mTvJobsFilter, 1)
            } else if (clickType == 2) {
                showProposlasFilterMenu(mTvJobsFilter, 1)
            }


        }


    }

    override fun onLeads(it: LeadsModel) {
        rvProposalsAna.visibility = View.GONE
        rvProjectsAnalyticsHome.visibility = View.VISIBLE
        mTvOngoing.setText(it.data.countFollowUpLeads.toString())
        mTvPending.setText(it.data.countPendingLeads.toString())
        mTvTotal.setText((it.data.counttotalLeads+it.data.countcompletedLeads).toString())
        mTvCompleted.setText(it.data.countcompletedLeads.toString())
        mList.clear()
        mList.addAll(it.data.leadsData)
        mHomeLeadsAdapter.notifyDataSetChanged()
    }

    override fun onJobsList(it: LeadsModel) {
        rvProposalsAna.visibility = View.GONE
        rvProjectsAnalyticsHome.visibility = View.VISIBLE
        mTvOngoing.setText(it.data.countongoingLeads.toString())
        mTvPending.setText(it.data.countPendingLeads.toString())
        mTvTotal.setText( (it.data.countPendingLeads + it.data.countongoingLeads + it.data.countcompletedLeads).toString()  )

        mTvCompleted.setText(it.data.countcompletedLeads.toString())
        mList.clear()
        mList.addAll(it.data.leadsData)
        mHomeLeadsAdapter.notifyDataSetChanged()
    }

    override fun onRevenueModel(it: RevenueModel) {
        if (it.data!!.details != null) {

            var mins = it.data.details.totalHours.toString().substringAfter(":")


            val formatter = DecimalFormat("#,###,###")
            mTvTotal.setText(formatter.format(it.data!!.details.totalProposalsCount))
            mTvPending.setText("$ " + formatter.format(it.data!!.details.totalProposalAmount))
            mTvOngoing.setText( formatter.format(it.data!!.details.totalConvertedJobsCount))
            mTvCompleted.setText("$ " + formatter.format(it.data!!.details.convertedJobProposalAmount))


            mTvMetricSales.setText("$ " + formatter.format(it.data!!.details.totalProposalAmount))
            mTvMetricExpense.setText("$ " + formatter.format(/*it.data!!.details.total_invoice_expenses + */it.data!!.details.total_proposal_expenses ))
            mTvMetricRevenue.setText("$ " + formatter.format(it.data!!.details.totalRevenue))
            mTvMetricNetIncome.setText("$ " + formatter.format(it.data!!.details.totalRevenue -(/*it.data!!.details.total_invoice_expenses +*/ it.data!!.details.total_proposal_expenses)))
            mTvMetricHr.setText(it.data.details.totalHours.substringBefore(":"))
            mTvMetricMin.setText(mins.toString().substringBefore(":"))

        }
    }

    override fun onJobMetricResponse(it: MetircJobDataModel) {
        if (it.data.jobsData!=null) {
            constraintLayout14.visibility = View.VISIBLE
            var mins = it.data.jobsData!!.totalHours.toString().substringAfter(":")

            val formatter = DecimalFormat("#,###,###")
            mTvMetricSales.setText("$ " + formatter.format(it.data.jobsData!!.Sales))
            mTvMetricRevenue.setText("$ " + formatter.format(it.data.jobsData!!.Revenue))
            mTvMetricExpense.setText("$ " + formatter.format(it.data!!.jobsData!!.total_expenses))
            mTvMetricNetIncome.setText("$ " + formatter.format(it.data!!.jobsData!!.NetIncome))
            mTvMetricHr.setText(it.data.jobsData!!.totalHours.substringBefore(":"))
            mTvMetricMin.setText(mins.toString().substringBefore(":"))
        }
    }

    override fun onProposals(it: PorposalsListModel) {
        if (it.data.proposal_list.isNotEmpty()) {
            rvProjectsAnalyticsHome.visibility = View.GONE
            rvProposalsAna.visibility = View.VISIBLE
            mListProposals.clear()
            mListProposals.addAll(it.data.proposal_list)
            mProposalsAdapter.notifyDataSetChanged()
        } else {

            toast("No record found.")
        }
    }

    override fun onDelete(it: SuccessModel) {
        toast("Deleted Successfully")
        mListProposals.removeAt(selected_position)
        mProposalsAdapter.notifyDataSetChanged()
    }

    /*for sales person data showing*/
    override fun onRevenueResponse(it: ReveneModel) {

    }

    override fun onListResponse(it: SignleUserJobsModel) {
        val formatter = DecimalFormat("#,###,###")

        if (clickType==0){

            mTvOngoing.setText(formatter.format(it.data.ongoingJobs).toString())
            mTvPending.setText(formatter.format(it.data.pendingJobs).toString())
            mTvTotal.setText( formatter.format(it.data.totalJobs).toString()  )
            mTvCompleted.setText(formatter.format(it.data.completedJobs).toString())
        }else{

            mTvOngoing.setText(formatter.format(it.data.ongoingJobs).toString())
            mTvPending.setText(it.data.pendingJobs.toString())
            mTvTotal.setText(formatter.format(it.data.totalJobs).toString())
            mTvCompleted.setText(formatter.format(it.data.completedJobs).toString())
        }
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun enableButton() {

    }

    override fun disableButton() {

    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (clickType == 2) {

            if (item.equals("Delete")) {
                AllinOneDialog(ttle = "Delete",
                    msg = "Are you sure you want to Delete it ?",
                    onLeftClick = {/*btn No click*/ },
                    onRightClick = {/*btn Yes click*/
                        selected_position = position
                        if (isInternetConnected()) {
                            presenter.deleteproposal(mList.get(position)._id)
                        }
                    })
            } else {
                if (mListProposals[position].invoice_url.isNotEmpty()) {
                    startActivity(
                        Intent(requireActivity(), PDFViewNewActivity::class.java)
                            .putExtra("pdfurl", mListProposals[position].invoice_url)
                            .putExtra("email", mListProposals[position].client_id.email)
                            .putExtra("id", mListProposals[position]._id)
                    )
                }
            }
        } else {
            mainActivity.addMainFragment(R.id.mainContainer, AnalyticsJobsFragment())
        }
    }

    override fun onSingleItemClick(item: Any, position: Int) {

    }

    fun showLogoutMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.dates_menu, popup.getMenu())
        popup.setOnMenuItemClickListener {
            mTvTopFilter.setText(it.title)
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

    fun showJobsFilterMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.jobs_menu, popup.getMenu())
        popup.setOnMenuItemClickListener {
            mTvJobsFilter.setText(it.title)
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

    fun showLeadsFilterMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.analyticsleads_menu, popup.getMenu())
        popup.setOnMenuItemClickListener {
            mTvJobsFilter.setText(it.title)
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }


    fun showProposlasFilterMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.proposalsdropdown_menu, popup.getMenu())
        popup.setOnMenuItemClickListener {
            mTvJobsFilter.setText(it.title)
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }


}