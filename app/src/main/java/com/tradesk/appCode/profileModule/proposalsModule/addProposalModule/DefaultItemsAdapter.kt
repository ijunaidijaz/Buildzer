package com.tradesk.appCode.profileModule.proposalsModule.addProposalModule

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.ItemsData
import com.tradesk.listeners.SingleItemCLickListener

class DefaultItemsAdapter(
    context: Context,
    val listener: SingleItemCLickListener,
    var mTasksDataFiltered: MutableList<ItemsData>) : RecyclerView.Adapter<DefaultItemsAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_defaultlist, parent, false)
        )
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {


            mTvTitle.setText(mTasksDataFiltered[position].title)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mTvDesc.setText(Html.fromHtml(mTasksDataFiltered[position].description, Html.FROM_HTML_MODE_COMPACT));
            } else {
                mTvDesc.setText(Html.fromHtml(mTasksDataFiltered[position].description));
            }



//            tvMarkComplete.text=if (mTasksDataFiltered[position].task_status.equals("Task Completed")) mTasksDataFiltered[position].task_status else "Mark As Complete"
////            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context,if(mTasksDataFiltered[position].task_status.equals("Mark As Complete")) R.color.blue_color else if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.pink_color))
//            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context, if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.blue_color))
//
//            tvMarkComplete.setOnClickListener {
//                if(mTasksDataFiltered[position].task_status.equals("Task Completed").not()){
//                    listener.onSingleListClick(mTasksDataFiltered[position],position)
//                }
            clParent.setOnClickListener { listener.onSingleItemClick("1",position)  }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val parent=view.findViewById<CardView>(R.id.parent)
        val clParent=view.findViewById<ConstraintLayout>(R.id.clParent)
        val mTvTitle=view.findViewById<TextView>(R.id.mTvTitle)
        val mTvDesc=view.findViewById<TextView>(R.id.mTvDesc)


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