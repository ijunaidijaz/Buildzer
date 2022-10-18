package com.tradesk.appCode.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.LeadsData
import com.tradesk.listeners.SingleItemCLickListener
import java.util.*

class HomeLeadsAdapter(
    context: Context,
    val listener: SingleItemCLickListener,
    var mTasksDataFiltered: MutableList<LeadsData>,
    var mTasksDataOriginal: MutableList<LeadsData>
) : RecyclerView.Adapter<HomeLeadsAdapter.VHolder>(), Filterable {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_home_leadsnewupdate, parent, false)
        )
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {

            if (mTasksDataFiltered.get(position).type.equals("job")) {
                mTvDate.setBackgroundResource(R.drawable.round_shape_sale)
                mTvDate.text = "Sale"

            } else {

                if (mTasksDataFiltered.get(position).status.equals("pending")) {
                    mTvDate.text = "Pending"
                    mTvDate.setBackgroundResource(R.drawable.round_shape_reload)
                } else if (mTasksDataFiltered.get(position).status.equals("follow_up")) {
                    mTvDate.setBackgroundResource(R.drawable.round_shape_coloredleads)
                    mTvDate.text = "Follow Up"
                } else if (mTasksDataFiltered.get(position).status.equals("sale")) {
                    mTvDate.setBackgroundResource(R.drawable.round_shape_sale)
                    mTvDate.text = "Sale"
                }
            }
            if (mTasksDataFiltered[position].client.isNotEmpty()) {
                mTvName.text = mTasksDataFiltered[position].client[0].name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }

                mTvPhone.text = insertString(mTasksDataFiltered[position].client[0].phone_no, "", 0)
                mTvPhone.text = insertString(mTvPhone.text.toString(), ")", 2)
                mTvPhone.text = insertString(mTvPhone.text.toString(), " ", 3)
                mTvPhone.text = insertString(mTvPhone.text.toString(), "-", 7)
                mTvPhone.text = "+1 (" + mTvPhone.text.toString()
            } else {
                mTvName.text = "N/A"
                mTvPhone.text = "N/A"
            }

            mTvTitle.text = mTasksDataFiltered[position].project_name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }

            mTvAddress.text =
                mTasksDataFiltered[position].address.street + ", " + mTasksDataFiltered[position].address.city+ ", " + mTasksDataFiltered[position].address.zipcode

//            tvMarkComplete.text=if (mTasksDataFiltered[position].task_status.equals("Task Completed")) mTasksDataFiltered[position].task_status else "Mark As Complete"
////            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context,if(mTasksDataFiltered[position].task_status.equals("Mark As Complete")) R.color.blue_color else if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.pink_color))
//            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context, if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.blue_color))
//
//            tvMarkComplete.setOnClickListener {
//                if(mTasksDataFiltered[position].task_status.equals("Task Completed").not()){
//                    listener.onSingleListClick(mTasksDataFiltered[position],position)
//                }
            parent.setOnClickListener { listener.onSingleItemClick("1", position) }
            mIvNavigate.setOnClickListener {
                listener.onSingleItemClick(
                    "2",
                    position
                )
            } //navigate click
            mIvEmail.setOnClickListener { listener.onSingleItemClick("3", position) } //email click
            mIvCall.setOnClickListener { listener.onSingleItemClick("4", position) }  //phone click

        }
    }
    override fun getFilter(): Filter {
        return object : Filter(){
            var result= FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()){
//                    mClientListFiltered=mTasksData
                    result.count=mTasksDataOriginal.size
                    result.values=mTasksDataOriginal
                    return result
                }else{
                    val listing= mutableListOf<LeadsData>()
                    mTasksDataOriginal.forEach {
                        if (it.project_name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
//                            ||it.address.city.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
                            || it.client[0].name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))){
                            listing.add(it)
                        }
                    }

                    result.count=listing.size
                    result.values=listing
                    return result
                }
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                mTasksDataFiltered= p1!!.values as MutableList<LeadsData>
                notifyDataSetChanged()
            }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mClNavigate = view.findViewById<ConstraintLayout>(R.id.mClNavigate)
        val parent = view.findViewById<CardView>(R.id.parent)
        val mTvDate = view.findViewById<TextView>(R.id.mTvDate)
        val mTvName = view.findViewById<TextView>(R.id.mTvName)
        val mTvTitle = view.findViewById<TextView>(R.id.mTvTitle)
        val mTvPhone = view.findViewById<TextView>(R.id.mTvPhone)
        val mTvAddress = view.findViewById<TextView>(R.id.mTvAddress)
        val mIvEmail = view.findViewById<ImageView>(R.id.mIvEmail)
        val mIvCall = view.findViewById<ImageView>(R.id.mIvCall)
        val mIvNavigate = view.findViewById<ImageView>(R.id.mIvNavigate)

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