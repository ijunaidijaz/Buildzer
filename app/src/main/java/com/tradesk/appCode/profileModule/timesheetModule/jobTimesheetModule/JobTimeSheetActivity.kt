package com.tradesk.appCode.profileModule.timesheetModule.jobTimesheetModule

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.profileModule.timesheetModule.TimesheetActivity
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.toast
import kotlinx.android.synthetic.main.activity_job_time_sheet.crpv
import kotlinx.android.synthetic.main.activity_job_time_sheet.mIvBack
import kotlinx.android.synthetic.main.activity_job_time_sheet.mTvHrMiTime
import kotlinx.android.synthetic.main.activity_job_time_sheet.mTvHrTime
import kotlinx.android.synthetic.main.activity_job_time_sheet.mTvMiTime
import kotlinx.android.synthetic.main.activity_job_time_sheet.mTvSec
import kotlinx.android.synthetic.main.activity_job_time_sheet.rvTimeSheet
import kotlinx.android.synthetic.main.activity_job_time_sheet.tvClickOn
import kotlinx.android.synthetic.main.activity_job_time_sheet.tvStatuses
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class JobTimeSheetActivity : BaseActivity(), IJobTimesheetView, SingleListCLickListener {


    var job_id = ""
    var mHomeImage = true
    var CheckVersion = true

    var addresss = ""
    var city = ""
    var state = ""
    var country = ""
    var postalCode = ""
    var knownName = ""

    var status = ""
    var api_status = ""
    var enddate = ""
    var enddate2 = ""
    var server_save_date = ""

    var total_ring_minutes = ""
    var clockoutday = ""


    val mList = mutableListOf<JobLogTimeNewTimeSheet>()
//    val mList = mutableListOf<DailyTimeLogNewTimeSheet>()
//    val mTimeSheetAdapter by lazy { TimeSheetDetailAdapter(this,this,mList) }
    val mTimeSheetAdapter by lazy { TimeSheetDaysListAdapter(this,this,mList) }


    @Inject
    lateinit var presenter: JobTimesheetPresenter<IJobTimesheetView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_time_sheet)
        presenter.onAttach(this)

        val tz = TimeZone.getDefault()



        rvTimeSheet.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rvTimeSheet.adapter = mTimeSheetAdapter

        mIvBack.setOnClickListener { finish() }

        if (!mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")){
            tvStatuses.visibility= View.GONE
            if (isInternetConnected()) {
                presenter.timesheetlist("1", "10", mPrefs.getKeyValue(PreferenceConstants.USER_ID))
            }
        }else{
            tvStatuses.visibility= View.GONE
            if (isInternetConnected()) {
                presenter.timesheetlist("1", "10", "")
            }
        }

        if (isInternetConnected()){
            presenter.jobsdetailtimesheet(intent.getStringExtra("id").toString())
        }

        tvClickOn.setOnClickListener {
            if (status.equals("clocked_out")) {

                val geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(this, Locale.getDefault())

                addresses = geocoder.getFromLocation(
                    mPrefs.getKeyValue(PreferenceConstants.LAT).toDouble(),
                    mPrefs.getKeyValue(PreferenceConstants.LNG).toDouble(),
//                    ("34.0628345").toDouble(), ("-118.3018231").toDouble(),
                    1
                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


//                      addresss = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                if (!addresses[0].featureName.isNotEmpty()) {
                    addresss = addresses[0].featureName + ", " + addresses[0].thoroughfare
                } else {
                    if (addresses[0].thoroughfare!==null) {
                        addresss = addresses[0].thoroughfare
                    }else{
                        addresss = addresses[0].getLocality()
                    }
                }
                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                city = addresses[0].getLocality()
                state = addresses[0].getAdminArea()
                country = addresses[0].getCountryName()
                postalCode = addresses[0].getPostalCode()

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                enddate = sdf.format(Date())

                if (checkedDate(server_save_date).equals(checkedDate(enddate))) {
                    if (isInternetConnected()) {
                        presenter.outTime(
                            job_id,
                            "clocked_out",
                            server_save_date,
                            enddate,
                            tz.id,
                            addresss,
                            city,
                            state,
                            postalCode,
                            mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
                                PreferenceConstants.LNG
                            )
                        )
                    }
                } else {
                    clockoutday = "1"
                    if (isInternetConnected()) {
                        presenter.outTime(
                            job_id,
                            "clocked_out",
                            server_save_date,
                            checkedDate(server_save_date) + " 23:59:58",
                            tz.id,
                            addresss,
                            city,
                            state,
                            postalCode,
                            mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
                                PreferenceConstants.LNG
                            )
                        )
                    }
                }
            } else {
                job_id = intent!!.getStringExtra("id").toString()

                val geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(this, Locale.getDefault())

                addresses = geocoder.getFromLocation(
                    mPrefs.getKeyValue(PreferenceConstants.LAT).toDouble(),
                    mPrefs.getKeyValue(PreferenceConstants.LNG).toDouble(),
                    1
                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                if (!addresses[0].featureName.isNotEmpty()) {
                    addresss = addresses[0].featureName + ", " + addresses[0].thoroughfare
                } else {
                    if (addresses[0].thoroughfare!==null) {
                        addresss = addresses[0].thoroughfare
                    }else{
                        addresss = addresses[0].getLocality()
                    }
                }
                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                city = addresses[0].getLocality()
                state = addresses[0].getAdminArea()
                country = addresses[0].getCountryName()
                postalCode = addresses[0].getPostalCode()
//                      knownName = addresses[0].getFeatureName()

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                enddate = sdf.format(Date())
                val tz = TimeZone.getDefault()
                if (isInternetConnected()) {
                    presenter.addtime(
                        job_id,
                        "clocked_in",
                        enddate,
                        tz.id,
                        addresss,
                        city,
                        state,
                        postalCode,
                        mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
                            PreferenceConstants.LNG
                        )
                    )
                }

            }

        }
    }

    override fun onDetail(it: TimeModelNewUPdate) {

        if (it.data.total_time != null) {
            var mins = it.data.total_time.totalLogTime.toString().substringAfter(":")
            var sec = mins.toString().substringAfter(":")

            mTvHrMiTime.setText("00:00:00")

//            mTvHrTime.setText(it.data.total_time.totalLogTime.toString().substringBefore(":"))
//            mTvMiTime.setText(mins.toString().substringBefore(":"))
//            mTvSec.setText(sec)
        } else {
            mTvHrMiTime.setText("00:00:00")
            mTvHrTime.setText("00")
            mTvMiTime.setText("00")
        }

        if (it.data.clockedInJob!=null ) {
            if ( it.data.clockedInJob.isNotEmpty()){
                job_id = it.data.clockedInJob[0].job_id

                server_save_date = it.data.clockedInJob.get(0).start_date
                if (it.data.clockedInJob.get(0).status.equals("clocked_in")) {
                    status = "clocked_out"
                    tvClickOn.setText("Clock Out")
                    if (isInternetConnected()) {

                        val geocoder: Geocoder
                        val addresses: List<Address>
                        geocoder = Geocoder(this, Locale.getDefault())
                        addresses = geocoder.getFromLocation(
                            mPrefs.getKeyValue(PreferenceConstants.LAT).toDouble(),
                            mPrefs.getKeyValue(PreferenceConstants.LNG).toDouble(),
//                        ("34.0628345").toDouble(), ("-118.3018231").toDouble(),
                            1
                        )
                        if (!addresses[0].featureName.isNotEmpty()) {
                            addresss = addresses[0].featureName + ", " + addresses[0].thoroughfare
                        } else {
                            if (addresses[0].thoroughfare!==null) {
                                addresss = addresses[0].thoroughfare
                            }else{
                                addresss = addresses[0].getLocality()
                            }
                        }
                        city = addresses[0].getLocality()
                        state = addresses[0].getAdminArea()
                        country = addresses[0].getCountryName()
                        postalCode = addresses[0].getPostalCode()

                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        enddate = sdf.format(Date())
                        val tz = TimeZone.getDefault()

                        if (tz.id.equals(it.data.clockedInJob.get(0).timezone)) {
                            if (checkedDate(server_save_date).equals(checkedDate(enddate))) {
                                if (isInternetConnected()) {
                                    presenter.gettime(
                                        job_id,
                                        "clocked_in",
                                        server_save_date,
                                        enddate,
                                        tz.id,
                                        addresss,
                                        city,
                                        state,
                                        postalCode,
                                        mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
                                            PreferenceConstants.LNG
                                        )
                                    )
                                }
                            } else {
                                clockoutday = "80"
                                if (isInternetConnected()) {
                                    presenter.outTime(
                                        job_id,
                                        "clocked_out",
                                        server_save_date,
                                        checkedDate(server_save_date) + " 23:59:58",
                                        it.data.clockedInJob.get(0).timezone,
                                        addresss,
                                        city,
                                        state,
                                        postalCode,
                                        mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
                                            PreferenceConstants.LNG
                                        )
                                    )
                                }

                            }
                        } else {

                            clockoutday = "1"
                            if (isInternetConnected()) {
                                presenter.outTime(
                                    job_id,
                                    "clocked_out",
                                    server_save_date,
                                    checkedDate(server_save_date) + " 23:59:58",
                                    it.data.clockedInJob.get(0).timezone,
                                    addresss,
                                    city,
                                    state,
                                    postalCode,
                                    mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
                                        PreferenceConstants.LNG
                                    )
                                )
                            }
                        }
                    }
                } else {
                    tvClickOn.setText("Clock In")
                    status = "clocked_in"
                }
            }
        } else {
            tvClickOn.setText("Clock In")
            status = "clocked_in"
        }
    }

    override fun onGetResponse(it: ClockInOutModel) {
        if (it.data.log_time.isNotEmpty()) {
            var mins = it.data.log_time.toString().substringAfter(":")
            var sec = mins.toString().substringAfter(":")
            mTvHrMiTime.setText(
                it.data.log_time.toString().replace("-", "")
                    .substringBefore(":") + " hr " + mins.toString().substringBefore(":") + " mi "+ sec + " sec"
            )
            if (it.data.log_time.toString().replace("-", "").substringBefore(":").toInt() > 0) {
                crpv.percent = (it.data.log_time.toString().replace("-", "")
                    .substringBefore(":") + "." + mins.substringAfter(":")).toFloat()
            } else {
                crpv.percent = ("0" + "." + mins.substringAfter(":")).toFloat()
            }

        } else {
            mTvHrMiTime.setText("00:00:00")
            mTvHrTime.setText("00")
            mTvMiTime.setText("00")
        }


    }

    override fun onInResponse(it: ClockInOutModel) {
        toast("Time Clocked In")
        status = "clocked_out"
        server_save_date = it.data.create_jobs_logs.start_date
        tvClickOn.setText("Clock Out")
    }

    override fun onOutResponse(it: ClockInOutModel) {
        toast("Time Clocked Out")
        status = "clocked_in"
        tvClickOn.setText("Clock In")
        if (clockoutday.equals("1")) {
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(this, Locale.getDefault())
            addresses = geocoder.getFromLocation(
                mPrefs.getKeyValue(PreferenceConstants.LAT).toDouble(),
                mPrefs.getKeyValue(PreferenceConstants.LNG).toDouble(),
//                ("34.0628345").toDouble(), ("-118.3018231").toDouble(),
                1
            )
            if (!addresses[0].featureName.isNotEmpty()) {
                addresss = addresses[0].featureName + ", " + addresses[0].thoroughfare
            } else {
                if (addresses[0].thoroughfare!==null) {
                    addresss = addresses[0].thoroughfare
                }else{
                    addresss = addresses[0].getLocality()
                }
            }
            city = addresses[0].getLocality()
            state = addresses[0].getAdminArea()
            country = addresses[0].getCountryName()
            postalCode = addresses[0].getPostalCode()
//                      knownName = addresses[0].getFeatureName()

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            enddate = sdf.format(Date())
            val tz = TimeZone.getDefault()
            if (isInternetConnected()) {
                presenter.addtime(
                    job_id,
                    "clocked_in",
                    enddate,
                    tz.id,
                    addresss,
                    city,
                    state,
                    postalCode,
                    mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
                        PreferenceConstants.LNG
                    )
                )
            }
            clockoutday = ""
        } else if (clockoutday.equals("80")) {
            tvClickOn.setText("Clock In")
            status = "clocked_in"
            clockoutday = ""
            if (isInternetConnected()) {
                if (!mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")){
                    presenter.timesheetlist("1", "20", mPrefs.getKeyValue(PreferenceConstants.USER_ID))
                }else{
                    presenter.timesheetlist("1", "20", "")
                }
            }
            if (isInternetConnected()){
                presenter.jobsdetailtimesheet(intent.getStringExtra("id").toString())
            }
        } else {
            if (isInternetConnected()) {
                if (!mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")){
                    presenter.timesheetlist("1", "20", mPrefs.getKeyValue(PreferenceConstants.USER_ID))
                }else{
                    presenter.timesheetlist("1", "20", "")
                }
            }
            if (isInternetConnected()){
                presenter.jobsdetailtimesheet(intent.getStringExtra("id").toString())
            }
        }
    }

    override fun onDetails(it: NewTimeSheetModelClass) {

        var mins = it.data.job_details[0].JobSheettotalTime.toString().substringAfter(":")
        var sec = mins.toString().substringAfter(":")

        mTvHrTime.setText(it.data.job_details[0].JobSheettotalTime.toString().substringBefore(":"))
        mTvMiTime.setText(mins.toString().substringBefore(":"))
        mTvSec.setText(sec)

        mList.clear()
        mList.addAll(it.data.job_details[0].job_log_time)
        mTimeSheetAdapter.notifyDataSetChanged()
    }

    override fun onerror(data: String) {
         toast(data)
    }

    override fun onGeneratedToken(lastAction: String) {

    }

    override fun onSingleListClick(item: Any, position: Int) {
        startActivity(Intent(this, TimesheetActivity::class.java)
            .putExtra("id",intent.getStringExtra("id").toString())
            .putExtra("date",mList[position].start_date)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2222) {
                if (resultCode == Activity.RESULT_OK) {

                    job_id = data!!.getStringExtra("result").toString()

                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(this, Locale.getDefault())

                    addresses = geocoder.getFromLocation(
                        mPrefs.getKeyValue(PreferenceConstants.LAT).toDouble(),
                        mPrefs.getKeyValue(PreferenceConstants.LNG).toDouble(),
                        1
                    ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    if (!addresses[0].featureName.isNotEmpty()) {
                        addresss = addresses[0].featureName + ", " + addresses[0].thoroughfare
                    } else {
                        if (addresses[0].thoroughfare!==null) {
                            addresss = addresses[0].thoroughfare
                        }else{
                            addresss = addresses[0].getLocality()
                        }
                    }
                    // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                    city = addresses[0].getLocality()
                    state = addresses[0].getAdminArea()
                    country = addresses[0].getCountryName()
                    postalCode = addresses[0].getPostalCode()
//                      knownName = addresses[0].getFeatureName()

                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    enddate = sdf.format(Date())
                    val tz = TimeZone.getDefault()
                    if (isInternetConnected()) {
                        presenter.addtime(
                            job_id,
                            "clocked_in",
                            enddate,
                            tz.id,
                            addresss,
                            city,
                            state,
                            postalCode,
                            mPrefs.getKeyValue(PreferenceConstants.LAT) + ", " + mPrefs.getKeyValue(
                                PreferenceConstants.LNG
                            )
                        )
                    }

                }


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }

    }

    private fun checkedDate(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
            spf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
}