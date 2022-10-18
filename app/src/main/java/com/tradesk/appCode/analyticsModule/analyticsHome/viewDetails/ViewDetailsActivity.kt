package com.tradesk.appCode.analyticsModule.analyticsHome.viewDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.analyticsModule.analyticsHome.AnalyticsProjectsHomeAdapter
import com.tradesk.data.entity.LeadsData
import com.tradesk.listeners.SingleItemCLickListener
import kotlinx.android.synthetic.main.activity_view_details.*
import kotlinx.android.synthetic.main.activity_view_details.mTvJobsFilter
import kotlinx.android.synthetic.main.activity_view_details.mTvTitleSectionBelowRecyclerView
import kotlinx.android.synthetic.main.activity_view_details.mTvTitleSectionThreeBelowRecyclerView
import kotlinx.android.synthetic.main.activity_view_details.mTvTitleSectionTwoBelowRecyclerView
import kotlinx.android.synthetic.main.activity_view_details.rvProjectsAnalyticsHome
import kotlinx.android.synthetic.main.fragment_analytics_home.*

class ViewDetailsActivity : AppCompatActivity(), SingleItemCLickListener {

    val mList = mutableListOf<LeadsData>()
    val mHomeLeadsAdapter by lazy { AnalyticsProjectsHomeAdapter(this,this,mList) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_details)
        mIvBack.setOnClickListener { finish() }

        if (intent.getStringExtra("clickType").equals("0")){

            if (intent.getStringExtra("title").equals("First")) {
                mTvTitleSectionBelowRecyclerView.setText("Total Jobs")
                mTvTitleSectionTwoBelowRecyclerView.setText("Project Name")
                mTvTitleSectionThreeBelowRecyclerView.setText("Description")
                mTvJobsFilter.setText("All")
            }else if (intent.getStringExtra("title").equals("Second")){
                mTvTitleSectionBelowRecyclerView.setText("Pending Jobs")
                mTvTitleSectionTwoBelowRecyclerView.setText("Project Name")
                mTvTitleSectionThreeBelowRecyclerView.setText("Description")
                mTvJobsFilter.setText("All")
            }else if (intent.getStringExtra("title").equals("Third")){
                mTvTitleSectionBelowRecyclerView.setText("Ongoing Jobs")
                mTvTitleSectionTwoBelowRecyclerView.setText("Project Name")
                mTvTitleSectionThreeBelowRecyclerView.setText("Description")
                mTvJobsFilter.setText("All")
            }else if (intent.getStringExtra("title").equals("Fourth")){
                mTvTitleSectionBelowRecyclerView.setText("Completed Jobs")
                mTvTitleSectionTwoBelowRecyclerView.setText("Project Name")
                mTvTitleSectionThreeBelowRecyclerView.setText("Description")
                mTvJobsFilter.setText("All")
            }
        }else if (intent.getStringExtra("clickType").equals("1")){
            if (intent.getStringExtra("title").equals("First")) {
                mTvTitleSectionBelowRecyclerView.setText("Total Leads")
                mTvTitleSectionTwoBelowRecyclerView.setText("Job Title")
                mTvTitleSectionThreeBelowRecyclerView.setText("Client")
                mTvJobsFilter.setText("All")
            }else if (intent.getStringExtra("title").equals("Second")){
                mTvTitleSectionBelowRecyclerView.setText("Winning Leads")
                mTvTitleSectionTwoBelowRecyclerView.setText("Job Title")
                mTvTitleSectionThreeBelowRecyclerView.setText("Client")
                mTvJobsFilter.setText("All")
            }else if (intent.getStringExtra("title").equals("Third")){
                mTvTitleSectionBelowRecyclerView.setText("Junk Leads")
                mTvTitleSectionTwoBelowRecyclerView.setText("Job Title")
                mTvTitleSectionThreeBelowRecyclerView.setText("Client")
                mTvJobsFilter.setText("All")
            }else if (intent.getStringExtra("title").equals("Fourth")){
                mTvTitleSectionBelowRecyclerView.setText("Follow Ups")
                mTvTitleSectionTwoBelowRecyclerView.setText("Job Title")
                mTvTitleSectionThreeBelowRecyclerView.setText("Client")
                mTvJobsFilter.setText("All")
            }
        }else if (intent.getStringExtra("clickType").equals("2")){
            if (intent.getStringExtra("title").equals("First")) {
                mTvTitleSectionBelowRecyclerView.setText("Total Proposals")
                mTvTitleSectionTwoBelowRecyclerView.setText("Proposal Title")
                mTvTitleSectionThreeBelowRecyclerView.setText("Client Name")
                mTvJobsFilter.setText("All")
            }else if (intent.getStringExtra("title").equals("Second")){
                mTvTitleSectionBelowRecyclerView.setText("Converted Jobs")
                mTvTitleSectionTwoBelowRecyclerView.setText("Proposal Title")
                mTvTitleSectionThreeBelowRecyclerView.setText("Client Name")
                mTvJobsFilter.setText("All")
            }else if (intent.getStringExtra("title").equals("Third")){
                mTvTitleSectionBelowRecyclerView.setText("Sent to Revenue")
                mTvTitleSectionTwoBelowRecyclerView.setText("Proposal Title")
                mTvTitleSectionThreeBelowRecyclerView.setText("Client Name")
                mTvJobsFilter.setText("All")
            }else if (intent.getStringExtra("title").equals("Fourth")){
                mTvTitleSectionBelowRecyclerView.setText("Converted Revenue")
                mTvTitleSectionTwoBelowRecyclerView.setText("Proposal Title")
                mTvTitleSectionThreeBelowRecyclerView.setText("Client Name")
                mTvJobsFilter.setText("All")
            }
        }

        mTvJobsFilter.setOnClickListener {
            if (intent.getStringExtra("clickType").equals("0")){
                showJobsFilterMenu(mTvJobsFilter,1)
            }else if (intent.getStringExtra("clickType").equals("1")){
                showLeadsFilterMenu(mTvJobsFilter,1)
            }else if (intent.getStringExtra("clickType").equals("2")){
                showProposlasFilterMenu(mTvJobsFilter,1)
            }


        }

        rvProjectsAnalyticsHome.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvProjectsAnalyticsHome.adapter = mHomeLeadsAdapter

    }

    override fun onSingleItemClick(item: Any, position: Int) {

    }

    fun showJobsFilterMenu(anchor: View, position: Int ): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.jobs_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{
            mTvJobsFilter.setText(it.title)
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

    fun showLeadsFilterMenu(anchor: View, position: Int ): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.analyticsleads_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{
            mTvJobsFilter.setText(it.title)
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

    fun showProposlasFilterMenu(anchor: View, position: Int ): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.proposalsdropdown_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{
            mTvJobsFilter.setText(it.title)
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

}