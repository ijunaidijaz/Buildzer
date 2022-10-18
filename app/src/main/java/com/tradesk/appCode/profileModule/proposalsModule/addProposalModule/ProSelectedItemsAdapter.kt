package com.tradesk.appCode.profileModule.proposalsModule.addProposalModule

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.listeners.OnItemRemove
import com.tradesk.listeners.SingleItemCLickListener

class ProSelectedItemsAdapter(
    context: Context,
    val listener: SingleItemCLickListener,
    val removelistener: OnItemRemove,
    var mItems: MutableList<AddItemDataUpdateProposal>) : RecyclerView.Adapter<ProSelectedItemsAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_porposalitems, parent, false)
        )
    }

    override fun getItemCount(): Int = mItems.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {


            tvName.setText(mItems[position].title)
            tvPrice.setText("$"+mItems[position].amount)
            quantity.setText(""+mItems[position].quantity)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvDesc.setText(Html.fromHtml(mItems[position].description, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvDesc.setText(Html.fromHtml(mItems[position].description));
            }



//            tvMarkComplete.text=if (mTasksDataFiltered[position].task_status.equals("Task Completed")) mTasksDataFiltered[position].task_status else "Mark As Complete"
////            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context,if(mTasksDataFiltered[position].task_status.equals("Mark As Complete")) R.color.blue_color else if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.pink_color))
//            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context, if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.blue_color))
//
//            tvMarkComplete.setOnClickListener {
//                if(mTasksDataFiltered[position].task_status.equals("Task Completed").not()){
//                    listener.onSingleListClick(mTasksDataFiltered[position],position)
//                }
            editItem.setOnClickListener { listener.onSingleItemClick("1",position)  }
            cross.setOnClickListener { removelistener.onRemove("cancel",position)  }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val editItem=view.findViewById<LinearLayout>(R.id.editItem)
        val tvName=view.findViewById<TextView>(R.id.tvName)
        val tvDesc=view.findViewById<TextView>(R.id.tvDesc)
        val quantity=view.findViewById<TextView>(R.id.quantity)
        val tvPrice=view.findViewById<TextView>(R.id.tvPrice)
        val cross=view.findViewById<ImageView>(R.id.cross)


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