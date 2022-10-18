package com.tradesk.appCode.profileModule.timesheetModule.jobTimesheetModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleListCLickListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimeSheetDaysListAdapter(
    context: Context,
    val listener: SingleListCLickListener,
   var mTasksDataFiltered: MutableList<JobLogTimeNewTimeSheet>) : RecyclerView.Adapter<TimeSheetDaysListAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_dayslisttimesheet, parent, false)
        )
    }

    override fun getItemCount(): Int =  mTasksDataFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {

            var mins =mTasksDataFiltered[position].total_time.toString().substringAfter(":")
//            mTvJobTime.text=mTasksDataFiltered[position].total_time.toString().substringBefore(":")+" hrs"+mins.toString().substringBefore(":")+" min"

            mTvJobTime.setText(mTasksDataFiltered[position].total_time.toString().substring(0,2)
                    +"h "+mTasksDataFiltered[position].total_time.toString().substring(3,5)+"m "+mTasksDataFiltered[position].total_time.toString().substring(6,8)+"s")


             mTvDate.text=convertDateFormat(mTasksDataFiltered[position].start_date)



            parent.setOnClickListener { listener.onSingleListClick("Detail",position) }
            mTvView.setOnClickListener { listener.onSingleListClick("Detail",position) }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val parent=view.findViewById<CardView>(R.id.parent)
        val mTvJobTime=view.findViewById<TextView>(R.id.mTvJobTime)
        val mTvDate=view.findViewById<TextView>(R.id.mTvDate)
        val mTvView=view.findViewById<TextView>(R.id.mTvView)




    }

    private fun convertDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
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