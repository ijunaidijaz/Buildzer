package com.tradesk.appCode.profileModule.timesheetModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.JobsData
import com.tradesk.listeners.SingleListCLickListener

class TimeSheetAdapter(
    context: Context,
    val listener: SingleListCLickListener,
   var mTasksDataFiltered: MutableList<JobsData>) : RecyclerView.Adapter<TimeSheetAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_timesheetcopy, parent, false)
        )
    }

    override fun getItemCount(): Int =  mTasksDataFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            if (position==0){
                mTvTimeSheetDate.visibility=View.GONE
            }else{
                mTvTimeSheetDate.visibility=View.GONE
            }
            mTvJobTitle.text=mTasksDataFiltered[position].project_name
            mTvJobTime.setText(mTasksDataFiltered[position].JobSheettotalTime.toString().substring(0,2)
                    +"h "+mTasksDataFiltered[position].JobSheettotalTime.toString().substring(3,5)+"m "+mTasksDataFiltered[position].JobSheettotalTime.toString().substring(6,8)+"s")

            mTvJobPhone.text=mTasksDataFiltered[position].client_data[0].name
            mTvJobAddress.text=mTasksDataFiltered[position].address.street+", "+mTasksDataFiltered[position].address.city

            parent.setOnClickListener { listener.onSingleListClick("Detail",position) }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mTvTimeSheetDate=view.findViewById<TextView>(R.id.mTvTimeSheetDate)
        val parent=view.findViewById<CardView>(R.id.parent)
        val mTvJobTitle=view.findViewById<TextView>(R.id.mTvJobTitle)
        val mTvJobTime=view.findViewById<TextView>(R.id.mTvJobTime)
        val mTvJobPhone=view.findViewById<TextView>(R.id.mTvJobPhone)
        val mTvJobAddress=view.findViewById<TextView>(R.id.mTvJobAddress)


    }

    open fun insertString(
        originalString: String,
        stringToBeInserted: String?,
        index: Int
    ): String? {
        // Create a new string
        var newString: String? = String()
        for (i in 0 until originalString.length) {
            // Insert the original string character
            // into the new string
            newString += originalString[i]
            if (i == index) {
                // Insert the string to be inserted
                // into the new string
                newString += stringToBeInserted
            }
        }
        // return the modified String
        return newString
    }

}