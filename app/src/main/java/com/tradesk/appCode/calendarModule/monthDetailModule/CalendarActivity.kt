package com.tradesk.appCode.calendarModule.monthDetailModule

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.appCode.calendarModule.CalendarItemsAdapter
import com.tradesk.appCode.calendarModule.ICalendarView
import com.tradesk.base.BaseActivity
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleItemCLickListener
import com.tradesk.utils.extension.toast
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.activity_calendar.mIvAddCal
import kotlinx.android.synthetic.main.activity_calendar.rvCalendarItems
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CalendarActivity : BaseActivity(), ICalendarDetqilView, View.OnClickListener, ICalendarView,
    SingleItemCLickListener {
    val weekList = arrayListOf<String>(
        "SUN",
        "MON",
        "TUE",
        "WED",
        "THU",
        "FRI",
        "SAT",
    )
    val mInfoBuilderTwo: Dialog by lazy { Dialog(this) }
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)

    val dateReminderCountList = arrayListOf<ReminderCount>()

    val today = Calendar.getInstance(Locale.ENGLISH).timeInMillis.getFormatTime("yyyy-MM-dd")
    val currentMonth = Calendar.getInstance(Locale.ENGLISH)
    val prevMonth = Calendar.getInstance(Locale.ENGLISH).apply { add(Calendar.MONTH, -1) }
    val NextMonth = Calendar.getInstance(Locale.ENGLISH).apply { add(Calendar.MONTH, +1) }
    var selectedDate = "NA"
    var date_type = 2
    var date_selected = ""

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.name.startsWith(
                "android.webkit."
            )
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.left - scrcoords[0]
            val y = ev.rawY + view.top - scrcoords[1]
            if (x < view.left || x > view.right || y < view.top || y > view.bottom)
            //  mAppUtils.hideSoftKeyboard(window.decorView.rootView)
                hideKeyboard(window.decorView.rootView)
        }
        return super.dispatchTouchEvent(ev)
    }

    @Inject
    lateinit var presenter: CalendarDetailPresenter<ICalendarDetqilView>

    val mList = mutableListOf<RemainderData>()
    val mHomeLeadsAdapter by lazy { CalendarItemsAdapter(this,this,mList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        presenter.onAttach(this)

        rvCalendarItems.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvCalendarItems.adapter = mHomeLeadsAdapter

        getReminderData(1,2)
        onClicks()
        showMonthViews()
        setupRVAdapter()
        mIvAddCal.setOnClickListener { showFollowUpPop(0) }
    }

    private fun getReminderData(date:Int=1,type:Int=2) {

        if (isInternetConnected()) {
            val tz = TimeZone.getDefault()
            presenter.calendardetail(tz.id, currentMonth.also {
                it.set(Calendar.DAY_OF_MONTH,
                    date)
            }.timeInMillis.getFormatTime("yyyy-MM-dd"), "month")
        }

        if (type==1){
            date_type=1
            date_selected = currentMonth.also { it.set(Calendar.DAY_OF_MONTH, date) }.timeInMillis.getFormatTime("yyyy-MM-dd")
        }

    }


    private fun onClicks() {
        mIvBack.setOnClickListener(this)
        tvPrev.setOnClickListener(this)
        tvNext.setOnClickListener(this)
//        btnGetStarted.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!) {
            mIvBack -> finish()
            tvPrev -> updateMonths(false)
            tvNext -> updateMonths(true)
        }
    }

    private fun updateMonths(isNext: Boolean) {
        prevMonth.add(Calendar.MONTH, if (isNext) +1 else -1)
        currentMonth.add(Calendar.MONTH, if (isNext) +1 else -1)
        NextMonth.add(Calendar.MONTH, if (isNext) +1 else -1)

        selectedDate = currentMonth.also { it.set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis.getFormatTime("yyyy-MM-dd")
        if (today.substringBeforeLast("-")==selectedDate.substringBeforeLast("-")){
            selectedDate = today
        }
        showMonthViews()

    }

    private fun showMonthViews() {
//        tvPrev.text = prevMonth.timeInMillis.getFormatTime("MMM")
        tvCurrent.text = currentMonth.timeInMillis.getFormatTime("MMM yyyy")
//        tvNext.text = NextMonth.timeInMillis.getFormatTime("MMM")
        getReminderData(1,2)
        generateCurrentData()
    }

    private fun addEmptyDateSpaceIfNeeded() = arrayListOf<Pair<String, String>>().apply {
        val first = Calendar.getInstance(Locale.ENGLISH).apply {
                timeInMillis = currentMonth.also { it.set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
            }
                .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        repeat((1.until(getWeekNum(first!!))).count()) { add(Pair("", "")) }
    }


    private fun generateCurrentData() {
        val dates = mutableListOf<Pair<String, String>>().also {
            it.addAll(addEmptyDateSpaceIfNeeded())
            (1..currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)).forEach { dt ->
                val ymd = currentMonth.also {
                    it.set(Calendar.DAY_OF_MONTH,
                        dt)
                }.timeInMillis.getFormatTime("yyyy-MM-dd")
                it.add(Pair(if (dt < 10) "0$dt" else "$dt", ymd))
            }
        }

        calendarMonth.adapter =
            DisplayAdapter(R.layout.item_reminder_cal_day, dates.size) { adpter1, holder, pos ->
                (holder as DisplayAdapter.DisplayHolder).view.apply {
                    findViewById<RecyclerView>(R.id.rvAdherenceCalDots)!!.also {
                        val reminders =
                            dateReminderCountList.filter {
                                dates[pos].second.isNotEmpty() && it.date.contains(dates[pos].second)
                            }
                        if (dateReminderCountList.isNotEmpty() && reminders.isNotEmpty()) {
                            (it.layoutManager as GridLayoutManager).spanCount=if (reminders[0].total_reminders>3) 3 else reminders[0].total_reminders
                            it.adapter = DisplayAdapter(R.layout.item_reminder_cal_dots,
                                reminders[0].total_reminders) { adapter2, holder, pos ->
                                (holder as DisplayAdapter.DisplayHolder).view.apply {}
                            }
                        }else it.adapter=null
                    }
                    findViewById<CheckedTextView>(R.id.tvDayName).also {
                        it.isVisible = dates[pos].first.isNotEmpty()
                        it.isChecked = dates[pos].second == selectedDate || today==dates[pos].second
                        it.text =
                            if (dates[pos].first.isNotEmpty()) "${dates[pos].first.toInt()}" else ""
                        it.setOnClickListener {
                            if (dates[pos].first.isEmpty()) return@setOnClickListener
                            selectedDate = dates[pos].second
                            getReminderData(dates[pos].first.toInt(),1)
                            adpter1.notifyDataSetChanged()
                        }
                    }
                }
            }
    }


    private fun setupRVAdapter() {
        calendarWeeks.adapter =
            DisplayAdapter(
                R.layout.item_reminder_cal_week_header,
                weekList.size
            ) { adapter, holder, pos ->
                holder.itemView.findViewById<TextView>(R.id.tvWeekName).text = weekList[pos]
            }
    }

    override fun onDrugSearched(it: RemindersModel) {
        mList.clear()
        mList.addAll(it.data.remainderData)
        mHomeLeadsAdapter.notifyDataSetChanged()
    }

    override fun onList(it: CalendarDetailModel) {
        dateReminderCountList.clear()
        dateReminderCountList.addAll(it.data.reminderCount)
        calendarMonth.adapter?.notifyDataSetChanged() ?: return

        if (date_type==1){
            if (isInternetConnected()) {
                val tz = TimeZone.getDefault()
                presenter.getremindersdate( selectedDate, "1","50",tz.id,)
            }
        }else{
            val tz = TimeZone.getDefault()
            if (isInternetConnected()){
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val todaydate = sdf.format(Date())
                System.out.println(" C DATE is  "+currentDate)
                presenter.getremindersdate(todaydate+ " 00:00:00","1","50",tz.id)
            }
        }

    }

    override fun onerror(data: String) {
        toast(data)
    }

    override fun onGeneratedToken(lastAction: String) {

    }


    fun String.getFormatTime(from: String, to: String): String {
        var sdf = SimpleDateFormat(from, Locale.ENGLISH);
        val date = sdf.parse(this)
        sdf = SimpleDateFormat(to, Locale.ENGLISH);
        try {
            return sdf.format(date);
        } catch (e1: ParseException) {
            e1.printStackTrace();
        }
        return ""
    }

    fun Long.getFormatTime(to: String): String {
        val sdf = SimpleDateFormat(to, Locale.ENGLISH);
        val cal = Calendar.getInstance(Locale.getDefault())
        cal.timeInMillis = this
        try {
            return sdf.format(cal.time);
        } catch (e1: ParseException) {
            e1.printStackTrace();
        }
        return ""
    }

    fun getWeekNum(week: String) = when (week.toUpperCase(Locale.ENGLISH)) {
        "SUNDAY" -> Calendar.SUNDAY
        "MONDAY" -> Calendar.MONDAY
        "TUESDAY" -> Calendar.TUESDAY
        "WEDNESDAY" -> Calendar.WEDNESDAY
        "THURSDAY" -> Calendar.THURSDAY
        "FRIDAY" -> Calendar.FRIDAY
        "SATURDAY" -> Calendar.SATURDAY
        else -> Calendar.SUNDAY
    }

    override fun onSingleItemClick(item: Any, position: Int) {

        val tz = TimeZone.getDefault()
        if (isInternetConnected()){
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val todaydate = sdf.format(Date())
            System.out.println(" C DATE is  "+currentDate)
            presenter.getremindersdate(todaydate+ " 00:00:00","1","20",tz.id)
        }
    }
    fun showFollowUpPop(id: Int) {
        mInfoBuilderTwo.setContentView(R.layout.popup_addingcalender);
        val displayMetrics = DisplayMetrics()
        mInfoBuilderTwo.window!!.getAttributes().windowAnimations = R.style.DialogAnimationNew
        mInfoBuilderTwo.window!!.setGravity(Gravity.BOTTOM)
       this.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
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
                this,
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
                        this,
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
                this,
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
                       this,
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

}