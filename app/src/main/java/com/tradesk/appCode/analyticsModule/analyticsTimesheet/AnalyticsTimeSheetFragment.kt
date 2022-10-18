package com.tradesk.appCode.analyticsModule.analyticsTimesheet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.R
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.jobsModule.jobDetailModule.OnGoingJobsActivity
import com.tradesk.appCode.profileModule.timesheetModule.TimeSheetAdapter
import com.tradesk.base.BaseFragment
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.*
import kotlinx.android.synthetic.main.fragment_timesheet.*
import kotlinx.android.synthetic.main.fragment_timesheet.tvStatuses
import java.util.*
import javax.inject.Inject
import android.location.Geocoder
import com.tradesk.data.entity.*
import java.text.ParseException
import java.text.SimpleDateFormat
import com.tradesk.appCode.profileModule.timesheetModule.jobTimesheetModule.JobTimeSheetActivity


class AnalyticsTimeSheetFragment : BaseFragment(), SingleListCLickListener, ITimeSheetView {
    /////////////
    private val TRIGGER_AUTO_COMPLETE: Int = 100
    private val AUTO_COMPLETE_DELAY: Long = 500
    private var handler: Handler? = null

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
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }


    val mList = mutableListOf<JobsData>()
    val mListSales = mutableListOf<Client>()
    val mTimeSheetAdapter by lazy { TimeSheetAdapter(requireActivity(), this, mList) }

    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity)
            mainActivity = context
    }

    //////////////
    val searchDrugData by lazy { arrayListOf<String>() }


    @Inject
    lateinit var presenter: TimesheetPresenter<ITimeSheetView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_timesheet, container, false)

    override fun setUp(view: View) {
        presenter.onAttach(this)

        val tz = TimeZone.getDefault()
        System.out.println(
            "TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT)
                .toString() + " Timezone id :: " + tz.id
        )


        rvTimeSheet.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvTimeSheet.adapter = mTimeSheetAdapter

        if (!mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")){
            tvStatuses.visibility=View.GONE
            if (isInternetConnected()) {
                presenter.timesheetlist("1", "20", mPrefs.getKeyValue(PreferenceConstants.USER_ID))
            }
        }else{
            tvStatuses.visibility=View.VISIBLE

            if (isInternetConnected()){
            presenter.userslist("1","30","")
            }

            if (isInternetConnected()) {
                presenter.timesheetlist("1", "20", "")
            }
        }
        tvStatuses.setOnClickListener { showSalesUsersMenu(tvStatuses, 1) }
        tvClickOn.setOnClickListener {
            if (status.equals("clocked_out")) {

                val geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(activity, Locale.getDefault())

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
                if (mPrefs.getKeyValue(PreferenceConstants.LAT).isNotEmpty()) {
                    val i = Intent(activity, OnGoingJobsActivity::class.java)
                    i.putExtra("from", "timesheet")
                    startActivityForResult(i, 2222)
                }
            }
        }




    }


    override fun onGeneratedToken(lastAction: String) {

    }

    override fun enableButton() {

    }

    override fun disableButton() {

    }

    override fun onSingleListClick(item: Any, position: Int) {
        requireActivity().startActivity(Intent(requireActivity(), JobTimeSheetActivity::class.java).putExtra("id", mList.get(position)._id)
        )
    }

    override fun onDetail(it: TimeModelNewUPdate) {

        if (it.data.total_time != null) {
            var mins = it.data.total_time.totalLogTime.toString().substringAfter(":")
            var sec = mins.toString().substringAfter(":")
//            mTvHrMiTime.setText(
//                it.data.total_time.totalLogTime.toString().replace("-", "")
//                    .substringBefore(":") + " hrs " + mins.toString().substringBefore(":") + " min"
//            )

            mTvHrMiTime.setText("00:00:00")

            mTvHrTime.setText(it.data.total_time.totalLogTime.toString().substringBefore(":"))
            mTvMiTime.setText(mins.toString().substringBefore(":"))
            mTvSec.setText(sec)
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
                    geocoder = Geocoder(activity, Locale.getDefault())
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
        mList.clear()
        mList.addAll(it.data.jobsData)
        mTimeSheetAdapter.notifyDataSetChanged()


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
            geocoder = Geocoder(activity, Locale.getDefault())
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
        } else {
            if (isInternetConnected()) {
                if (!mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")){
                    presenter.timesheetlist("1", "20", mPrefs.getKeyValue(PreferenceConstants.USER_ID))
                }else{
                    presenter.timesheetlist("1", "20", "")
                }
            }
        }
    }

    override fun onSubUsersResponse(it: ClientsListModel) {
        mListSales.clear()
        mListSales.addAll(it.data.client)
    }

    override fun onResume() {
        super.onResume()

    }


    fun showDropDownMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.getMenuInflater().inflate(R.menu.users_menu, popup.getMenu())
        popup.setOnMenuItemClickListener {
            tvStatuses.setText(it.title)
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2222) {
                if (resultCode == Activity.RESULT_OK) {

                    job_id = data!!.getStringExtra("result").toString()

                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(activity, Locale.getDefault())

                    addresses = geocoder.getFromLocation(
                        mPrefs.getKeyValue(PreferenceConstants.LAT).toDouble(),
                        mPrefs.getKeyValue(PreferenceConstants.LNG).toDouble(),

//                        ("34.0628345").toDouble(), ("-118.3018231").toDouble(),
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

    private fun convertDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
            spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun showSalesUsersMenu(anchor: View, position: Int): Boolean {
        val popup = PopupMenu(anchor.context, anchor)
        popup.menu.add("Select User")
        for (i in mListSales.indices) {
            popup.menu.add(mListSales[i].name)
        }

        popup.setOnMenuItemClickListener {
            if (it.title.equals("Select User")) {
                tvStatuses.setText(it.title)
            } else {
                tvStatuses.setText(it.title)
                if (isInternetConnected()) {
                    var id = ""
                    for (i in 0 until mListSales.size) {
                        if (mListSales.get(i).name.equals(it.title)) {
                            id = mListSales.get(i)._id
                        }

                    }
                    if (isInternetConnected()) {
//                        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")){
//                            presenter.timesheetlist("1", "20", mPrefs.getKeyValue(PreferenceConstants.USER_ID))
//                        }else{
                            presenter.timesheetlist("1", "100", mListSales.get(position)._id)
//                        }
                    }
                }
            }
            return@setOnMenuItemClickListener true

        }
        popup.show()
        return true
    }

}