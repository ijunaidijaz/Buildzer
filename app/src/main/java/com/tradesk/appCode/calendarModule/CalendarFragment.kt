package com.tradesk.appCode.calendarModule

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.tradesk.R
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.calendarModule.monthDetailModule.CalendarActivity
import com.tradesk.appCode.home.leadsDetail.LeadsActivity
import com.tradesk.base.BaseFragment
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.PreferenceConstants
import com.tradesk.utils.extension.*
import java.util.*
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.text.SimpleDateFormat

class CalendarFragment : BaseFragment(), SingleListCLickListener, ICalendarView, SingleItemCLickListener {

    val mInfoBuilderTwo: Dialog by lazy { Dialog(requireActivity()) }
    private val lastDayInCalendar = Calendar.getInstance(Locale.ENGLISH)
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)

    // current date
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]

    // selected date
    private var selectedDay: Int = currentDay
    private var selectedMonth: Int = currentMonth
    private var selectedYear: Int = currentYear

    // all days in month
    private val dates = ArrayList<Date>()



    var CheckVersion = true
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }


    val mList = mutableListOf<RemainderData>()
    val mHomeLeadsAdapter by lazy { CalendarItemsAdapter(requireActivity(),this,mList) }

    var selectdate=""
    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity)
            mainActivity = context
    }

    //////////////
    val searchDrugData by lazy { arrayListOf<String>() }


    @Inject
    lateinit var presenter: CalendarPresenter<ICalendarView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_calendar, container, false)

    override fun setUp(view: View) {
        presenter.onAttach(this)

        val tz = TimeZone.getDefault()

        /**
         * Adding SnapHelper here, but it is not needed. I add it just to looks better.
         */
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(calendar_recycler_view)

        /**
         * This is the maximum month that the calendar will display.
         * I set it for 6 months, but you can increase or decrease as much you want.
         */
        lastDayInCalendar.add(Calendar.MONTH, 6)

        setUpCalendar()

        /**
         * Go to the previous month. First, make sure the current month (cal)
         * is after the current date so that you can't go before the current month.
         * Then subtract  one month from the sludge. Finally, ask if cal is equal to the current date.
         * If so, then you don't want to give @param changeMonth, otherwise changeMonth as cal.
         */
        mIvPrevious!!.setOnClickListener {
//            if (cal.after(currentDate)) {
                cal.add(Calendar.MONTH, -1)
                if (cal == currentDate)
                    setUpCalendar()
                else
                    setUpCalendar(changeMonth = cal)
//            }
        }

        /**
         * Go to the next month. First check if the current month (cal) is before lastDayInCalendar,
         * so that you can't go after the last possible month. Then add one month to cal.
         * Then put @param changeMonth.
         */
        mIvNext!!.setOnClickListener {
            if (cal.before(lastDayInCalendar)) {
                cal.add(Calendar.MONTH, 1)
                setUpCalendar(changeMonth = cal)
            }
        }


        mIvAddLead.setOnClickListener { startActivity(Intent(activity, CalendarActivity::class.java)) }
        mIvAddCal.setOnClickListener { showFollowUpPop(0) }

        rvCalendarItems.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        rvCalendarItems.adapter = mHomeLeadsAdapter

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val todaydate = sdf.format(Date())
        System.out.println(" C DATE is  "+currentDate)

//        if (isInternetConnected()){
//            presenter.getremindersdate(todaydate,"1","20",tz.id)
//        }
    }




    override fun onGeneratedToken(lastAction: String) {

    }

    override fun enableButton() {

    }

    override fun disableButton() {

    }

    override fun onSingleListClick(item: Any, position: Int) {

    }

    override fun onDrugSearched(it: RemindersModel) {
        mList.clear()
        mList.addAll(it.data.remainderData)
        mHomeLeadsAdapter.notifyDataSetChanged()
    }



    override fun onAddReminder(it: SuccessModel) {
        toast("Reminder setup successfully")
        mInfoBuilderTwo.dismiss()
        val tz = TimeZone.getDefault()
        if (isInternetConnected()){
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val todaydate = sdf.format(Date())
            System.out.println(" C DATE is  "+currentDate)
            presenter.getremindersdate(todaydate+ " 00:00:00","1","20",tz.id)
        }
    }



    override fun onResume() {
        super.onResume()

    }

    override fun onSingleItemClick(item: Any, position: Int) {
        requireActivity().startActivity(Intent(requireActivity(),LeadsActivity::class.java))
    }


    /**
     * @param changeMonth I am using it only if next or previous month is not the current month
     */
    private fun setUpCalendar(changeMonth: Calendar? = null) {
        mTvCurrentMonth!!.text = sdf.format(cal.time)
        textView47!!.text = sdf.format(cal.time).substringAfter(" ")
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        /**
         *
         * If changeMonth is not null, then I will take the day, month, and year from it,
         * otherwise set the selected date as the current date.
         */
        selectedDay =
            when {
                changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
                else -> currentDay
            }
        selectedMonth =
            when {
                changeMonth != null -> changeMonth[Calendar.MONTH]
                else -> currentMonth
            }
        selectedYear =
            when {
                changeMonth != null -> changeMonth[Calendar.YEAR]
                else -> currentYear
            }

        var currentPosition = 0
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        /**
         * Fill dates with days and set currentPosition.
         * currentPosition is the position of first selected day.
         */
        while (dates.size < maxDaysInMonth) {
            // get position of selected day
            if (monthCalendar[Calendar.DAY_OF_MONTH] == selectedDay)
                currentPosition = dates.size
            dates.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Assigning calendar view.
        val layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        calendar_recycler_view!!.layoutManager = layoutManager
        val calendarAdapter = CalendarAdapter(requireActivity(), dates, currentDate, changeMonth)
        calendar_recycler_view!!.adapter = calendarAdapter

        /**
         * If you start the application, it centers the current day, but only if the current day
         * is not one of the first (1, 2, 3) or one of the last (29, 30, 31).
         */
        when {
            currentPosition > 2 -> calendar_recycler_view!!.scrollToPosition(currentPosition - 3)
            maxDaysInMonth - currentPosition < 2 -> calendar_recycler_view!!.scrollToPosition(currentPosition)
            else -> calendar_recycler_view!!.scrollToPosition(currentPosition)
        }


        /**
         * After calling up the OnClickListener, the text of the current month and year is changed.
         * Then change the selected day.
         */
        calendarAdapter.setOnItemClickListener(object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val clickCalendar = Calendar.getInstance()
                clickCalendar.time = dates[position]
                selectedDay = clickCalendar[Calendar.DAY_OF_MONTH]
                var newselectedMonth = 0
                if (clickCalendar[Calendar.MONTH]==12){
                    newselectedMonth = 1
                }else{
                    newselectedMonth = clickCalendar[Calendar.MONTH] + 1
                }

                var newselectedYear = clickCalendar[Calendar.YEAR]

//                toast(newselectedYear.toString() + "-"+newselectedMonth.toString()+"-" + selectedDay.toString())

                val tz = TimeZone.getDefault()
                if (isInternetConnected()){
                    if (newselectedMonth.toString().length==1) {
                        presenter.getremindersdate(newselectedYear.toString() + "-"+"0"+newselectedMonth.toString()+"-" + selectedDay.toString()+ " 00:00:00","1","20",tz.id)
                    }else if (selectedDay.toString().length==1){
                        presenter.getremindersdate(newselectedYear.toString() + "-"+newselectedMonth.toString()+"-" + "0"+selectedDay.toString()+ " 00:00:00","1","20",tz.id)
                    }else if(newselectedMonth.toString().length==1 && selectedDay.toString().length==1){
                        presenter.getremindersdate(newselectedYear.toString() + "-"+"0"+newselectedMonth.toString()+"-" +"0"+ selectedDay.toString()+ " 00:00:00","1","20",tz.id)
                    }else{
                        presenter.getremindersdate(newselectedYear.toString() + "-"+newselectedMonth.toString()+"-" + selectedDay.toString()+ " 00:00:00","1","20",tz.id)
                    }



                }
            }
        })
    }



    fun showFollowUpPop(id: Int) {
        mInfoBuilderTwo.setContentView(R.layout.popup_addingcalender);
        val displayMetrics = DisplayMetrics()
        mInfoBuilderTwo.window!!.getAttributes().windowAnimations = R.style.DialogAnimationNew
        mInfoBuilderTwo.window!!.setGravity(Gravity.BOTTOM)
        requireActivity().windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        mInfoBuilderTwo.window!!.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
            (displayMetrics.widthPixels * 0.99).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        var _pickedTime=""
        var _pickedDate=""
        var selected=""

        mInfoBuilderTwo.findViewById<TextView>(R.id.mTvCallReminder).setOnClickListener {

            val c: Calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                requireActivity(),
                { view, year, month, dayOfMonth ->
                    val _year = year.toString()
                    val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                    val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                    _pickedDate = "$_year-$_month-$_date"
                    Log.e("PickedDate: ", "Date: $_pickedDate") //2019-02-12

                    val c2 = Calendar.getInstance()
                    var mHour = c2[Calendar.HOUR_OF_DAY]
                    var mMinute = c2[Calendar.MINUTE]

                    // Launch Time Picker Dialog

                    // Launch Time Picker Dialog
                    val timePickerDialog = TimePickerDialog(
                        requireActivity(),
                        { view, hourOfDay, minute ->
                            mHour = hourOfDay
                            mMinute = minute
                            if (hourOfDay.toString().length==1 && minute.toString().length==1){
                                _pickedTime = "0"+hourOfDay.toString() + ":" + "0"+minute.toString()
                            }else if (hourOfDay.toString().length==1 ){
                                _pickedTime = "0"+hourOfDay.toString() + ":" +minute.toString()
                            }else if (minute.toString().length==1 ){
                                _pickedTime = hourOfDay.toString() + ":" +"0"+minute.toString()
                            }else{
                                _pickedTime = hourOfDay.toString() + ":" + minute.toString()
                            }
                            Log.e(
                                "PickedTime: ",
                                "Date: $_pickedDate" + _pickedTime
                            ) //2019-02-12
//                            et_show_date_time.setText(date_time.toString() + " " + hourOfDay + ":" + minute)

                            selected="1"
                            mInfoBuilderTwo.findViewById<TextView>(R.id.mTvCallReminder).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tickorange, 0);
                            mInfoBuilderTwo.findViewById<TextView>(R.id.mTvAppointReminder).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }, mHour, if (mMinute==60) 0+2 else if (mMinute==59) 0+1 else mMinute+2, false
                    )
                    timePickerDialog.show()

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.MONTH)
            )
            dialog.datePicker.minDate = System.currentTimeMillis() - 1000
            dialog.show()

        }

        mInfoBuilderTwo.findViewById<TextView>(R.id.mTvAppointReminder).setOnClickListener {

            val c: Calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                requireActivity(),
                { view, year, month, dayOfMonth ->
                    val _year = year.toString()
                    val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                    val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                    _pickedDate = "$_year-$_month-$_date"
                    Log.e("PickedDate: ", "Date: $_pickedDate") //2019-02-12

                    val c2 = Calendar.getInstance()
                    var mHour = c2[Calendar.HOUR_OF_DAY]
                    var mMinute = c2[Calendar.MINUTE]

                    // Launch Time Picker Dialog

                    // Launch Time Picker Dialog
                    val timePickerDialog = TimePickerDialog(
                        requireActivity(),
                        { view, hourOfDay, minute ->
                            mHour = hourOfDay
                            mMinute = minute
                            if (hourOfDay.toString().length==1 && minute.toString().length==1){
                                _pickedTime = "0"+hourOfDay.toString() + ":" + "0"+minute.toString()
                            }else if (hourOfDay.toString().length==1 ){
                                _pickedTime = "0"+hourOfDay.toString() + ":" +minute.toString()
                            }else if (minute.toString().length==1 ){
                                _pickedTime = hourOfDay.toString() + ":" +"0"+minute.toString()
                            }else{
                                _pickedTime = hourOfDay.toString() + ":" + minute.toString()
                            }

                            Log.e(
                                "PickedTime: ",
                                "Date: $_pickedDate" + _pickedTime
                            ) //2019-02-12
//                            et_show_date_time.setText(date_time.toString() + " " + hourOfDay + ":" + minute)


                            selected="2"

                            mInfoBuilderTwo.findViewById<TextView>(R.id.mTvAppointReminder).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tickorange, 0)
                            mInfoBuilderTwo.findViewById<TextView>(R.id.mTvCallReminder).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        }, mHour, if (mMinute==60) 0+2 else if (mMinute==59) 0+1 else mMinute+2, false
                    )
                    timePickerDialog.show()

                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.MONTH)
            )
            dialog.datePicker.minDate = System.currentTimeMillis() - 1000
            dialog.show()

        }
        mInfoBuilderTwo.findViewById<TextView>(R.id.tvCancel)
            .setOnClickListener {
                mInfoBuilderTwo.dismiss()
            }
         mInfoBuilderTwo.findViewById<TextView>(R.id.tvDone)
            .setOnClickListener {
                val tz = TimeZone.getDefault()
                if (selected.isNotEmpty()){
                    if (selected.equals("1")){

                            presenter.addreminder(
                                "phone",
                                "note",
                                _pickedDate + " " + _pickedTime+":00", mInfoBuilderTwo.findViewById<EditText>(R.id.mEtDescription).text.toString().trim(),tz.id
                            )


                    }else{
                            presenter.addreminder(
                                "appointment",
                                "note",
                                _pickedDate + " " + _pickedTime+":00", mInfoBuilderTwo.findViewById<EditText>(R.id.mEtDescription).text.toString().trim(),tz.id
                            )

                    }
                }
            }
        mInfoBuilderTwo.show();
    }

}