package com.tradesk.appCode.profileModule.gallaryModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.listeners.SingleListCLickListener

class GallaryAdapter(
    context: Context,
    val listener: SingleListCLickListener
  /*  var mTasksDataFiltered: MutableList<MyTaskItem>, val listener: SingleListCLickListener*/) : RecyclerView.Adapter<GallaryAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_gallery, parent, false)
        )
    }

    override fun getItemCount(): Int = /*mTasksDataFiltered.size*/4

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
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

//            clCard.setOnClickListener {
//                listener.onSingleListClick(clCard,position)
//             }

            ivMenu.setOnClickListener { listener.onSingleListClick(ivMenu,position) }
            ivMenuTwo.setOnClickListener { listener.onSingleListClick(ivMenuTwo,position) }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
//        val clCard=view.findViewById<CardView>(R.id.clCard)
//        val tvAssignDate=view.findViewById<TextView>(R.id.tvAssignDate)
//        val tvDueDate=view.findViewById<TextView>(R.id.tvDueDate)
//        val tvTaskDesc=view.findViewById<TextView>(R.id.tvTaskDesc)
        val ivMenu=view.findViewById<ImageView>(R.id.ivMenu)
        val ivMenuTwo=view.findViewById<ImageView>(R.id.ivMenuTwo)


    }

}