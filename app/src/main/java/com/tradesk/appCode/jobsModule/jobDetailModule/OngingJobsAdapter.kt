package com.tradesk.appCode.jobsModule.jobDetailModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.LeadsData
import com.tradesk.listeners.SingleListCLickListener
import java.util.*

class OngingJobsAdapter(
    context: Context, val listener: SingleListCLickListener,
    var mTasksDataFiltered: MutableList<LeadsData>
) : RecyclerView.Adapter<OngingJobsAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_jobs_new, parent, false)
        )
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            if (mTasksDataFiltered.get(position).status.equals("pending")) {
                mTvDate.text = "Pending"
                mTvDate.setBackgroundResource(R.drawable.round_shape_reload)
            } else if (mTasksDataFiltered.get(position).status.equals("ongoing")) {
                mTvDate.setBackgroundResource(R.drawable.round_shape_coloredleads)
                mTvDate.text = "Ongoing"
            } else if (mTasksDataFiltered.get(position).status.equals("completed")) {
                mTvDate.setBackgroundResource(R.drawable.round_shape_sale)
                mTvDate.text = "Completed"
            } else if (mTasksDataFiltered.get(position).status.equals("cancel")) {
                mTvDate.setBackgroundResource(R.drawable.round_shape_cancel)
                mTvDate.text = "Cancelled"
            }

            if (mTasksDataFiltered[position].client.isNotEmpty()) {
                mTvName.text = mTasksDataFiltered[position].client[0].name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
//                mTvNumber.text=mTasksDataFiltered[position].client[0].phone_no

                mTvNumber.text =
                    insertString(mTasksDataFiltered[position].client[0].phone_no, "", 0)
                mTvNumber.text = insertString(mTvNumber.text.toString(), ")", 2)
                mTvNumber.text = insertString(mTvNumber.text.toString(), " ", 3)
                mTvNumber.text = insertString(mTvNumber.text.toString(), "-", 7)
                mTvNumber.text = "+1 (" + mTvNumber.text.toString()
            } else {
                mTvName.text = "N/A"
                mTvNumber.text = "N/A"
            }

            mTvTitle.text = mTasksDataFiltered[position].project_name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            mTvAddress.text =
                mTasksDataFiltered[position].address.street + ", " + mTasksDataFiltered[position].address.city

//            if (position==0){
//                mTvDate.setText("Pending")
//                mTvDate.setBackgroundResource(R.drawable.round_shape_coloredleads)
//            }else if (position==1){
//                mTvDate.setBackgroundResource(R.drawable.round_shape_reload)
//                mTvDate.setText("Ongoing")
//            }else if (position==2){
//                mTvDate.setBackgroundResource(R.drawable.round_shape_sale)
//                mTvDate.setText("Complete")
//            } else{
//                mTvDate.setBackgroundResource(R.drawable.round_shape_reload)
//                mTvDate.setText("Ongoing")
//            }

//            tvName.text=mTasksDataFiltered[position].task_name
//            tvAssignDate.text=mTasksDataFiltered[position].assigned_on
//            tvDueDate.text=mTasksDataFiltered[position].due_date
//            tvTaskDesc.text=mTasksDataFiltered[position].description
//            tvMarkComplete.text=if (mTasksDataFiltered[position].task_status.equals("Task Completed")) mTasksDataFiltered[position].task_status else "Mark As Complete"
////            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context,if(mTasksDataFiltered[position].task_status.equals("Mark As Complete")) R.color.blue_color else if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.pink_color))
//            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context, if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.blue_color))
//
//            tvMarkComplete.setOnClickListener {
//                if(mTasksDataFiltered[position].task_status.equals("Task Completed").not()){
//                    listener.onSingleListClick(mTasksDataFiltered[position],position)
//                }

            parent.setOnClickListener { listener.onSingleListClick(parent, position) }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val parent = view.findViewById<CardView>(R.id.parent)
        val mTvDate = view.findViewById<TextView>(R.id.mTvDate)
        val mTvName = view.findViewById<TextView>(R.id.mTvName)
        val mTvTitle = view.findViewById<TextView>(R.id.mTvTitle)
        val mTvNumber = view.findViewById<TextView>(R.id.mTvNumber)
        val mTvAddress = view.findViewById<TextView>(R.id.mTvAddress)
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