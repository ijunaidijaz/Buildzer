package com.tradesk.appCode.calendarModule

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.RemainderData
import com.tradesk.listeners.SingleItemCLickListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CalendarItemsAdapter(
    val context: Context,
    val listener: SingleItemCLickListener,
   var mTasksDataFiltered: MutableList<RemainderData>/*, val listener: SingleListCLickListener*/) : RecyclerView.Adapter<CalendarItemsAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_calendaritems, parent, false)
        )
    }

    override fun getItemCount(): Int =  mTasksDataFiltered.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            if (mTasksDataFiltered[position].toString().equals("0")){
                mTvDate.visibility=View.VISIBLE
            }else{
                mTvDate.visibility=View.GONE
            }

            if (mTasksDataFiltered[position].remainder_type.equals("phone")){
                imageView54.setImageDrawable(context.getDrawable(R.drawable.ic_callphone))
            }else{
                imageView54.setImageDrawable(context.getDrawable(R.drawable.ic_calendarornagecolr))
            }

            if (mTasksDataFiltered[position].type.equals("note")){
                mTvDate.text=convertDateFormat(mTasksDataFiltered[position].dateTime)

                mTvName.text="Note : "+mTasksDataFiltered[position].description
                mTvtime.text=convertTimeDateFormat(mTasksDataFiltered[position].dateTime)
            }else{
                if (mTasksDataFiltered[position].client_id!=null&&mTasksDataFiltered[position].remainder_type.equals("phone")){
                    mTvDate.text=convertDateFormat(mTasksDataFiltered[position].dateTime)
                    mTvName.text="Call with "+mTasksDataFiltered[position].client_id!!.name
                    mTvtime.text=convertTimeDateFormat(mTasksDataFiltered[position].dateTime)
                }else if (mTasksDataFiltered[position].client_id!=null){
                    mTvDate.text=convertDateFormat(mTasksDataFiltered[position].dateTime)
                    mTvName.text="Appointment with "+mTasksDataFiltered[position].client_id!!.name
                    mTvtime.text=convertTimeDateFormat(mTasksDataFiltered[position].dateTime)
                }else{

                    mTvDate.text=convertDateFormat(mTasksDataFiltered[position].dateTime)
                    mTvName.text="Note : "+mTasksDataFiltered[position].description
                    mTvtime.text=convertTimeDateFormat(mTasksDataFiltered[position].dateTime)
                }
            }


        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView54=view.findViewById<ImageView>(R.id.imageView54)
        val mTvDate=view.findViewById<TextView>(R.id.mTvDate)
        val mTvName=view.findViewById<TextView>(R.id.mTvName)
        val mTvtime=view.findViewById<TextView>(R.id.mTvtime)
    }

    private fun convertDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
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
            spf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }




}