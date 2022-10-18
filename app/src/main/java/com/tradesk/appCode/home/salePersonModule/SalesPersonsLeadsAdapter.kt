package com.tradesk.appCode.home.salePersonModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.Client
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.extension.loadWallImage

class SalesPersonsLeadsAdapter(
    context: Context,
    var mClients: MutableList<Client>, val listener: SingleListCLickListener
) : RecyclerView.Adapter<SalesPersonsLeadsAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_salespersons, parent, false)
        )
    }

    override fun getItemCount(): Int = mClients.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {

            if (mClients.get(position).image.isNotEmpty()) {
                mIvPic.loadWallImage(mClients.get(position).image)
            }
            mTvName.text = mClients[position].name
            mTvEmail.text = mClients[position].email
            mTvAddress.text =
                mClients[position].address.city + ", " + mClients[position].address.state

            mTvPhone.text = insertString(mClients[position].phone_no.replace(" ", ""), "", 0)
            mTvPhone.text = insertString(mTvPhone.text.toString(), ")", 2)
            mTvPhone.text = insertString(mTvPhone.text.toString(), " ", 3)
            mTvPhone.text = insertString(mTvPhone.text.toString(), "-", 7)
            mTvPhone.text = "(" + mTvPhone.text.toString()

            tvStatus.setOnCheckedChangeListener { compoundButton, b ->
                listener.onSingleListClick(
                    "1",
                    position
                )
            }
//            tvMarkComplete.text=if (mTasksDataFiltered[position].task_status.equals("Task Completed")) mTasksDataFiltered[position].task_status else "Mark As Complete"
////            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context,if(mTasksDataFiltered[position].task_status.equals("Mark As Complete")) R.color.blue_color else if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.pink_color))
//            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context, if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.blue_color))
//
//            tvMarkComplete.setOnClickListener {
//                if(mTasksDataFiltered[position].task_status.equals("Task Completed").not()){
//                    listener.onSingleListClick(mTasksDataFiltered[position],position)
//                }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mTvName = view.findViewById<TextView>(R.id.mTvName)
        val tvStatus = view.findViewById<CheckBox>(R.id.tvStatus)
        val mTvPhone = view.findViewById<TextView>(R.id.mTvPhone)
        val mTvEmail = view.findViewById<TextView>(R.id.mTvEmail)
        val mTvAddress = view.findViewById<TextView>(R.id.mTvAddress)
        val mIvPic = view.findViewById<ImageView>(R.id.mIvPic)


    }

    // Function to insert string
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