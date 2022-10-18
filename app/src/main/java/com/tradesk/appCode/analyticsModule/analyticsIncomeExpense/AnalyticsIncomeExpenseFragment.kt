package com.tradesk.appCode.analyticsModule.analyticsIncomeExpense

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
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.addExpensesModule.ExpensesListActivity
import com.tradesk.appCode.jobsModule.jobDetailModule.JobDetailActivity
import com.tradesk.appCode.profileModule.proposalsModule.addProposalModule.PDFViewNewActivity
import com.tradesk.base.BaseFragment
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_proposals.*
import java.util.*
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_analytics_icomeexpense.*
import kotlinx.android.synthetic.main.fragment_analytics_icomeexpense.simpleTabLayout

class AnalyticsIncomeExpenseFragment : BaseFragment(), SingleListCLickListener,
    IAnalyticsIncomeExpenseView, SingleItemCLickListener {
    /////////////
    private val TRIGGER_AUTO_COMPLETE: Int = 100
    private val AUTO_COMPLETE_DELAY: Long = 500
    private var handler: Handler? = null

    val mList = mutableListOf<Proposal>()
    val mListExpenseJobs = mutableListOf<ExpensesExpensesJobs>()
    val mProposalsAdapter by lazy { AnalyticsInvoicesAdapter(requireActivity(), mList, this) }
    val mExoensesAdapter by lazy { AnalyticsExpenseJobsAdapter(requireActivity(), this,mListExpenseJobs) }
    var selected_position = 0
    var clicked = "0"
    var proposal_count = ""

    var mHomeImage = true
    var CheckVersion = true
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }


    val mHomeLeadsAdapter by lazy { AnalyticIncomeExpenseAdapter(requireActivity(), this) }

    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity)
            mainActivity = context
    }

    //////////////
    val searchDrugData by lazy { arrayListOf<String>() }


    @Inject
    lateinit var presenter: AnalyticsIncomeExpensePresenter<IAnalyticsIncomeExpenseView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_analytics_icomeexpense, container, false)

    override fun setUp(view: View) {
        presenter.onAttach(this)
        rvAnalyticsIncome.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvAnalyticsIncome12.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvAnalyticsIncome.adapter = mProposalsAdapter
        rvAnalyticsIncome12.adapter = mExoensesAdapter

        clicked = "0"
        mList.clear()
        mProposalsAdapter.notifyDataSetChanged()
        presenter.getProposals("1", "20", "", "invoice", "")
        mTvTitle.setText("Income Statistic")
        mTvIncomeView.setOnClickListener {
            mTvTitle.setText("Income Statistic")
            textView75.setText("Invoices")
            clExpense.visibility = View.GONE
            clIncome.visibility = View.VISIBLE

            mTvIncomeView.background = resources.getDrawable(R.drawable.appcolor_drawable_dark)
            mTvIncomeView.setTextColor(resources.getColor(R.color.white_color))

            mTvExpenseView.background = resources.getDrawable(R.drawable.white_drawable_dark)
            mTvExpenseView.setTextColor(resources.getColor(R.color.black_color))

            clicked = "0"
            mList.clear()
            mProposalsAdapter.notifyDataSetChanged()
            if (isInternetConnected()) {
                presenter.getProposals("1", "20", "", "invoice", "")
            }
        }
        mTvExpenseView.setOnClickListener {
            clIncome.visibility = View.GONE
            clExpense.visibility = View.VISIBLE
            mTvTitle.setText("Expense Statistic")
            textView75.setText("Expenses")
            mTvExpenseView.background = resources.getDrawable(R.drawable.appcolor_drawable_dark)
            mTvExpenseView.setTextColor(resources.getColor(R.color.white_color))


            mTvIncomeView.background = resources.getDrawable(R.drawable.white_drawable_dark)
            mTvIncomeView.setTextColor(resources.getColor(R.color.black_color))

            clicked = "1"
            mListExpenseJobs.clear()
            mExoensesAdapter.notifyDataSetChanged()
            if (isInternetConnected()) {
                presenter.getjobexpenseslist("1", "20")
            }
        }

        mTvIncomeRevenueFilters.setOnClickListener { showLogoutMenu(mTvIncomeRevenueFilters, 1) }

        val secTab: TabLayout.Tab = simpleTabLayout.newTab()
        secTab.text = "All"
        simpleTabLayout.addTab(secTab)

        val thirdTab: TabLayout.Tab = simpleTabLayout.newTab()
        thirdTab.text = "Pending"
        simpleTabLayout.addTab(thirdTab)

        val fourthTab: TabLayout.Tab = simpleTabLayout.newTab()
        fourthTab.text = "Ongoing"
        simpleTabLayout.addTab(fourthTab)


        val fiveTab: TabLayout.Tab = simpleTabLayout.newTab()
        fiveTab.text = "Complete"
        simpleTabLayout.addTab(fiveTab)

        simpleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        clicked = "0"
                        mList.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.getProposals("1", "20", "", "invoice", "")
                        }

                    }
                    1 -> {
                        clicked = "1"
                        mList.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.getProposals("1", "20", "pending", "invoice", "")
                        }
                    }
                    2 -> {
                        clicked = "2"
                        mList.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.getProposals("1", "30", "Inprocess", "invoice", "")
                        }
                    }
                    3 -> {
                        clicked = "3"
                        mList.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        if (isInternetConnected()) {
                            presenter.getProposals("1", "30", "completed", "invoice", "")
                        }


                    }

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        val secTab1: TabLayout.Tab = simpleTabLayout12.newTab()
        secTab1.text = "Jobs"
        simpleTabLayout12.addTab(secTab1)


        //    val mList = mutableListOf<DataShelters>()
        val NoOfEmp = mutableListOf<BarEntry>()

        NoOfEmp.add(BarEntry(945f, 0))
        NoOfEmp.add(BarEntry(1040f, 1))
        NoOfEmp.add(BarEntry(1133f, 2))
        NoOfEmp.add(BarEntry(1240f, 3))
        NoOfEmp.add(BarEntry(1369f, 4))
        NoOfEmp.add(BarEntry(1487f, 5))
        NoOfEmp.add(BarEntry(1501f, 6))
        NoOfEmp.add(BarEntry(1645f, 7))
        NoOfEmp.add(BarEntry(1578f, 8))
        NoOfEmp.add(BarEntry(1695f, 9))


        val year = ArrayList<String>()


        year.add("2014")
        year.add("2015")
        year.add("2016")
        year.add("2017")
        year.add("2018")
        year.add("2019")
        year.add("2020")
        year.add("2017")
        year.add("2021")
        year.add("2022")

        val bardataset = BarDataSet(NoOfEmp, "Total Revenue")
        chart1.animateY(5000)
        chart2.animateY(5000)
        val data = BarData(year, bardataset)
////        bardataset.setColors(ColorTemplate.COLORFUL_COLORS)
        bardataset.color = resources.getColor(R.color.colorAccent)
        chart1.setData(data)
        chart2.setData(data)

    }


    override fun onInvoicesResponse(it: PorposalsListModel) {
        if (it.data.proposal_list.isNotEmpty()) {
            mList.clear()
            mList.addAll(it.data.proposal_list)
            mProposalsAdapter.notifyDataSetChanged()
        } else {
            toast("No record found.")
        }
    }

    override fun onExpenseListResponse(it: ExpensesJobsListModel) {

    }


    override fun onGeneratedToken(lastAction: String) {

    }

    override fun enableButton() {

    }

    override fun disableButton() {

    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (item.equals("Invoices")) {
            if (mList[position].invoice_url.isNotEmpty()) {
                startActivity(
                    Intent(requireActivity(), PDFViewNewActivity::class.java)
                        .putExtra("pdfurl", mList[position].invoice_url)
                        .putExtra("email", mList[position].client_id.email)
                        .putExtra("id", mList[position]._id)
                )
            }
        } else if (item.equals("Detail")) {
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    JobDetailActivity::class.java
                ).putExtra("id", mListExpenseJobs.get(position)._id)
            )
        } else if (item.equals("Timesheet")) {
            startActivity(
                Intent(requireActivity(), ExpensesListActivity::class.java)
                    .putExtra("job_id", mListExpenseJobs[position]._id)
            )

        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onSingleItemClick(item: Any, position: Int) {

    }

    fun showLogoutMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.dates_menu, popup.getMenu())
        popup.setOnMenuItemClickListener {
            mTvIncomeRevenueFilters.setText(it.title)
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

}