package com.tradesk.appCode.profileModule.timesheetModule

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.DailyTimeLogNewTimeSheet
import com.tradesk.data.entity.NewTimeSheetModelClass
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.extension.toast
import kotlinx.android.synthetic.main.activity_timesheet.*
import javax.inject.Inject

class TimesheetActivity : BaseActivity(), SingleListCLickListener, ITimesheetDetailView {

    val mList = mutableListOf<DailyTimeLogNewTimeSheet>()
    val mTimeSheetAdapter by lazy { TimeSheetDetailAdapter(this,this,mList) }
    @Inject
    lateinit var presenter: TimesheetDetailPresenter<ITimesheetDetailView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet)
        presenter.onAttach(this)
        rvTimeSheet.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvTimeSheet.adapter = mTimeSheetAdapter

        mIvBack.setOnClickListener { finish() }
        if (isInternetConnected()){
            presenter.jobsdetailtimesheet(intent.getStringExtra("id").toString())
        }
    }

    override fun onDetails(it: NewTimeSheetModelClass) {
        for (i in 0 until it.data.job_details[0].job_log_time.size) {
            if (it.data.job_details[0].job_log_time[i].start_date.equals(intent.getStringExtra("date"))){
                mList.clear()
                mList.addAll(it.data.job_details[0].job_log_time[i].daily_time_log)
            }
        }
        mTimeSheetAdapter.notifyDataSetChanged()


    }

    override fun onerror(data: String) {
        toast(data)
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun onSingleListClick(item: Any, position: Int) {

    }
}