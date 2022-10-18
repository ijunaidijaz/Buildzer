package com.tradesk.appCode.analyticsModule.analyticsIncomeExpense

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.ExpensesExpensesJobs
import com.tradesk.listeners.SingleListCLickListener

class AnalyticsExpenseJobsAdapter(
    context: Context,val listener: SingleListCLickListener,
    var mTasksDataFiltered: MutableList<ExpensesExpensesJobs>) : RecyclerView.Adapter<AnalyticsExpenseJobsAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_jobs_new_expenses, parent, false)
        )
    }

    override fun getItemCount(): Int =  mTasksDataFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            if (mTasksDataFiltered.get(position).status.equals("pending")){
                mTvDate.setText("Pending")
                mTvDate.setBackgroundResource(R.drawable.round_shape_reload)
            }else if (mTasksDataFiltered.get(position).status.equals("ongoing")){
                mTvDate.setBackgroundResource(R.drawable.round_shape_coloredleads)
                mTvDate.setText("Ongoing")
            }else if (mTasksDataFiltered.get(position).status.equals("completed")){
                mTvDate.setBackgroundResource(R.drawable.round_shape_sale)
                mTvDate.setText("Complete")
            } else if (mTasksDataFiltered.get(position).status.equals("cancel")){
                mTvDate.setBackgroundResource(R.drawable.round_shape_cancel)
                mTvDate.setText("Cancelled")
            }

//            if (mTasksDataFiltered[position].client.isNotEmpty()){
//                mTvName.text=mTasksDataFiltered[position].client[0].name.capitalize()
////                mTvNumber.text=mTasksDataFiltered[position].client[0].phone_no
//
//                mTvNumber.text=insertString(mTasksDataFiltered[position].client[0].phone_no,"",0)
//                mTvNumber.text=insertString(mTvNumber.text.toString(),")",2)
//                mTvNumber.text=insertString(mTvNumber.text.toString()," ",3)
//                mTvNumber.text=insertString(mTvNumber.text.toString(),"-",7)
//                mTvNumber.text="+1 ("+mTvNumber.text.toString()
//            }else{
                mTvName.text="N/A"
                mTvNumber.text="N/A"
//            }

            mTvTitle.text=mTasksDataFiltered[position].project_name.capitalize()
            mTvAddress.text=mTasksDataFiltered[position].address.street+", "+mTasksDataFiltered[position].address.city

//            parent.setOnClickListener { listener.onSingleListClick(parent,position) }
            mTvJobDetail.setOnClickListener { listener.onSingleListClick("Detail",position) }
            mTvTimesheet.setOnClickListener { listener.onSingleListClick("Timesheet",position) }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val parent=view.findViewById<CardView>(R.id.parent)
        val mTvDate=view.findViewById<TextView>(R.id.mTvDate)
        val mTvName=view.findViewById<TextView>(R.id.mTvName)
        val mTvTitle=view.findViewById<TextView>(R.id.mTvTitle)
        val mTvNumber=view.findViewById<TextView>(R.id.mTvNumber)
        val mTvAddress=view.findViewById<TextView>(R.id.mTvAddress)
        val mTvJobDetail=view.findViewById<TextView>(R.id.mTvJobDetail)
        val mTvTimesheet=view.findViewById<TextView>(R.id.mTvTimesheet)
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