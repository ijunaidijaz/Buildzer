package com.tradesk.appCode.analyticsModule.analyticsUsers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.Client
import com.tradesk.listeners.SingleItemCLickListener

class AnalyticsUsersDetailAdapter(
    context: Context,
    val listener: SingleItemCLickListener,
     var mTasksDataFiltered: MutableList<Client> ) : RecyclerView.Adapter<AnalyticsUsersDetailAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_usersanalytics, parent, false)
        )
    }

    override fun getItemCount(): Int =  mTasksDataFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            mTvName.text=mTasksDataFiltered[position].name
            mTvEmail.text=mTasksDataFiltered[position].email
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
            parent.setOnClickListener { listener.onSingleItemClick("1",position)  }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val parent=view.findViewById<ConstraintLayout>(R.id.parent)
        val mTvName=view.findViewById<TextView>(R.id.mTvName)
        val mTvEmail=view.findViewById<TextView>(R.id.mTvEmail)
//        val tvDueDate=view.findViewById<TextView>(R.id.tvDueDate)
//        val tvTaskDesc=view.findViewById<TextView>(R.id.tvTaskDesc)
//        val tvMarkComplete=view.findViewById<TextView>(R.id.tvMarkComplete)


    }

}