package com.tradesk.appCode.profileModule.proposalsModule

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.BuildzerApp
import com.tradesk.R
import com.tradesk.data.entity.Proposal
import com.tradesk.listeners.SingleListCLickListener
import java.text.DecimalFormat
import java.util.*

class ProposalsAdapter(
    context: Context,
    var mTasksDataOriginal: MutableList<Proposal>,
    var mTasksDataFiltered: MutableList<Proposal>, val listener: SingleListCLickListener
) : RecyclerView.Adapter<ProposalsAdapter.VHolder>(), Filterable {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_proposals, parent, false)
        )
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            mTvClientName.text = "Client : " + mTasksDataFiltered[position].client_id.name

            val formatter = DecimalFormat("#,###,###")
            mTvTotal.setText(
                "$ " + formatter.format(
                    mTasksDataFiltered[position].total.replace(
                        ",",
                        ""
                    ).toInt()
                )
            )
//            mTvTotal.text="$ "+mTasksDataFiltered[position].total

            try {
//                val decodedBytes =
//                    Base64.getDecoder().decode(mTasksDataFiltered[position].items[0].description)
//                val decodedString = String(decodedBytes)
                mTvInfo.text = mTasksDataFiltered[position].items[0].description
            } catch (e: Exception) {
                Toast.makeText(BuildzerApp.appContext, e.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
//            val decodedBytes =
//                Base64.getDecoder().decode(mTasksDataFiltered[position].items[0].description)
//            val decodedString = String(decodedBytes)
//            mTvInfo.text = decodedString

            if (mTasksDataFiltered[position].status.equals("Inprocess")) {
//                mtvStatus.setText("Ongoing")
                mTvDate.setBackgroundResource(R.drawable.round_shape_coloredleads)
                mTvDate.setText("Ongoing")
            } else {
                if (mTasksDataFiltered.get(position).status.equals("pending")) {
                    mTvDate.setText("Pending")
                    mTvDate.setBackgroundResource(R.drawable.round_shape_reload)
                } else if (mTasksDataFiltered.get(position).status.equals("completed")) {
                    mTvDate.setBackgroundResource(R.drawable.round_shape_sale)
                    mTvDate.setText("Complete")
                }
//                mtvStatus.setText(mTasksDataFiltered[position].status.capitalize())
            }
            if (mTasksDataFiltered[position].type.equals("Invoice",true)) {
//                mtvStatus.setText("Ongoing")
//                mTvDate.setBackgroundResource(R.drawable.round_shape_coloredleads)
//                mTvDate.setText("Ongoing")
                edit_icon.visibility=View.GONE
            }



            maincard.setOnClickListener {
                listener.onSingleListClick(mTasksDataFiltered[position], position)
            }

            mIvDelete.setOnClickListener {
                listener.onSingleListClick("Delete", position)
            }
            edit_icon.setOnClickListener {
                listener.onSingleListClick("edit", position)
            }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mTvClientName = view.findViewById<TextView>(R.id.mTvClientName)
        val mTvTotal = view.findViewById<TextView>(R.id.mTvTotal)
        val mTvInfo = view.findViewById<TextView>(R.id.mTvInfo)
        val maincard = view.findViewById<CardView>(R.id.maincard)
        val mtvStatus = view.findViewById<TextView>(R.id.mtvStatus)
        val mTvDate = view.findViewById<TextView>(R.id.mTvDate)
        val mIvDelete = view.findViewById<ImageView>(R.id.mIvDelete)
        val edit_icon = view.findViewById<ImageView>(R.id.edit_icon)


    }

    override fun getFilter(): Filter {
        return object : Filter() {
            var result = FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()) {
//                    mClientListFiltered=mTasksData
                    result.count = mTasksDataOriginal.size
                    result.values = mTasksDataOriginal
                    return result
                } else {
                    val listing = mutableListOf<Proposal>()
                    mTasksDataOriginal.forEach {
                        if (it.client_id.name.toLowerCase(Locale.ENGLISH)
                                .contains(p0.toString().toLowerCase(Locale.ENGLISH))
                        /*|| it.client_details.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))*/) {
                            listing.add(it)
                        }
                    }

                    result.count = listing.size
                    result.values = listing
                    return result
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                mTasksDataFiltered = p1!!.values as MutableList<Proposal>
                notifyDataSetChanged()
            }
        }
    }
}