package com.tradesk.appCode.jobsModule.jobDetailModule

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.jobsModule.IJobsView
import com.tradesk.appCode.jobsModule.JobsPresenter
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.AppVersionEntity
import com.tradesk.data.entity.LeadsData
import com.tradesk.data.entity.LeadsModel
import com.tradesk.listeners.SingleListCLickListener
import kotlinx.android.synthetic.main.activity_on_going_jobs.*
import javax.inject.Inject

class OnGoingJobsActivity : BaseActivity(), SingleListCLickListener, IJobsView {

    val mList = mutableListOf<LeadsData>()
    val mJobsAdapter by lazy { OngingJobsAdapter(this, this, mList) }

    @Inject
    lateinit var presenter: JobsPresenter<IJobsView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_going_jobs)
        presenter.onAttach(this)

        rvJobs.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvJobs.adapter = mJobsAdapter

        if (isInternetConnected()) {
            if (intent.getStringExtra("from").equals("timesheet")) {
                presenter.getjobs("1", "20", "ongoing")
            } else {
                presenter.getjobs("1", "20", "all")
            }
        }

        mIvBack.setOnClickListener { finish() }
    }

    override fun onJobsList(it: LeadsModel) {
        mList.clear()
        mList.addAll(it.data.leadsData)
        mJobsAdapter.notifyDataSetChanged()
    }

    override fun onAppVersionResp(it: AppVersionEntity) {

    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun onSingleListClick(item: Any, position: Int) {
        val returnIntent = Intent()
        returnIntent.putExtra("result", mList[position]._id)
        returnIntent.putExtra("image", mList[position].image)
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}