package com.tradesk.appCode.profileModule.timesheetModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.DailyTimeLogNewTimeSheet
import com.tradesk.listeners.SingleListCLickListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimeSheetDetailAdapter(
    context: Context,
    val listener: SingleListCLickListener,
   var mTasksDataFiltered: MutableList<DailyTimeLogNewTimeSheet>) : RecyclerView.Adapter<TimeSheetDetailAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_timesheetdetail, parent, false)
        )
    }

    override fun getItemCount(): Int =  mTasksDataFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {

            var mins =mTasksDataFiltered[position].log_time.toString().substringAfter(":")
//            mTvJobTime.text=mTasksDataFiltered[position].total_time.toString().substringBefore(":")+" hrs"+mins.toString().substringBefore(":")+" min"

            mTvJobTime.setText(mTasksDataFiltered[position].log_time.toString().substring(0,2)
                    +"h "+mTasksDataFiltered[position].log_time.toString().substring(3,5)+"m "+mTasksDataFiltered[position].log_time.toString().substring(6,8)+"s")


            mTvInAddress.setText(mTasksDataFiltered[position].clockedIn_address.street+", "+mTasksDataFiltered[position].clockedIn_address.city)

            mTvDate.text=convertDateFormat(mTasksDataFiltered[position].start_date)
            mTvInTime.text=convertTimeDateFormat(mTasksDataFiltered[position].start_date)
            if (mTasksDataFiltered[position].end_date!=null){
                mTvOutTime.text=convertTimeDateFormat(mTasksDataFiltered[position].end_date.toString())
                mTvOutAddress.text=mTasksDataFiltered[position].clockedOut_address.street+", "+mTasksDataFiltered[position].clockedOut_address.city
            }else{
                mTvOutTime.text="N/A"
                mTvOutAddress.text="N/A"
            }


            parent.setOnClickListener { listener.onSingleListClick("Detail",position) }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val parent=view.findViewById<CardView>(R.id.parent)
        val mTvJobTime=view.findViewById<TextView>(R.id.mTvJobTime)
        val mTvInAddress=view.findViewById<TextView>(R.id.mTvInAddress)
        val mTvDate=view.findViewById<TextView>(R.id.mTvDate)
        val mTvInTime=view.findViewById<TextView>(R.id.mTvInTime)
        val mTvOutTime=view.findViewById<TextView>(R.id.mTvOutTime)
        val mTvOutAddress=view.findViewById<TextView>(R.id.mTvOutAddress)


    }

    private fun convertDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("MMM dd, yy", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun convertTimeDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }




}