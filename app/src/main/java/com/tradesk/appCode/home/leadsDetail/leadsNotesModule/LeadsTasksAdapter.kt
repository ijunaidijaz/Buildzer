package com.tradesk.appCode.home.leadsDetail.leadsNotesModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.listeners.CustomCheckBoxListener
import com.tradesk.listeners.SingleItemCLickListener

class LeadsTasksAdapter(
    context: Context,
    val listener: SingleItemCLickListener,
    val listenerTwo: CustomCheckBoxListener
    /*  var mTasksDataFiltered: MutableList<MyTaskItem>, val listener: SingleListCLickListener*/
) : RecyclerView.Adapter<LeadsTasksAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_taskslist, parent, false)
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

            tvStatus.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    listenerTwo.onCheckBoxClick(position)
                }
            }

//            parent.setOnClickListener { listener.onSingleItemClick("1",position)  }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val parent = view.findViewById<CardView>(R.id.parent)
        val tvStatus = view.findViewById<CheckBox>(R.id.tvStatus)
//        val tvAssignDate=view.findViewById<TextView>(R.id.tvAssignDate)
//        val tvDueDate=view.findViewById<TextView>(R.id.tvDueDate)
//        val tvTaskDesc=view.findViewById<TextView>(R.id.tvTaskDesc)
//        val tvMarkComplete=view.findViewById<TextView>(R.id.tvMarkComplete)


    }

}