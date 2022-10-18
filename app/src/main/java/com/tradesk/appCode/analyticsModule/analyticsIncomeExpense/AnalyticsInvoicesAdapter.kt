package com.tradesk.appCode.analyticsModule.analyticsIncomeExpense

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.Proposal
import com.tradesk.listeners.SingleListCLickListener
import java.text.DecimalFormat
import java.util.Base64
class AnalyticsInvoicesAdapter(
    context: Context,
    var mTasksDataFiltered: MutableList<Proposal>, val listener: SingleListCLickListener
) : RecyclerView.Adapter<AnalyticsInvoicesAdapter.VHolder>() {


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
            mTvClientName.text="Client : "+mTasksDataFiltered[position].client_id.name

            val formatter = DecimalFormat("#,###,###")
            mTvTotal.setText("$ "+formatter.format(mTasksDataFiltered[position].total.replace(",","").toInt()))
//            mTvTotal.text="$ "+mTasksDataFiltered[position].total


            val decodedBytes = Base64.getDecoder().decode(mTasksDataFiltered[position].items[0].description)
            val decodedString = String(decodedBytes)
            mTvInfo.text=decodedString

            if (mTasksDataFiltered[position].status.equals("Inprocess")){
                mtvStatus.setText("Ongoing")
            }else{
                mtvStatus.setText(mTasksDataFiltered[position].status.capitalize())
            }
            maincard.setOnClickListener {
                listener.onSingleListClick("Invoices", position)
            }

            mIvDelete.visibility=View.GONE
            mIvDelete.setOnClickListener {
                listener.onSingleListClick("Delete", position)
            }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mTvClientName=view.findViewById<TextView>(R.id.mTvClientName)
        val mTvTotal=view.findViewById<TextView>(R.id.mTvTotal)
        val mTvInfo=view.findViewById<TextView>(R.id.mTvInfo)
        val maincard=view.findViewById<CardView>(R.id.maincard)
        val mtvStatus=view.findViewById<TextView>(R.id.mtvStatus)
        val mIvDelete=view.findViewById<ImageView>(R.id.mIvDelete)


    }

}