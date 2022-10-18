package com.tradesk.appCode.profileModule.usersContModule

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.DataTradesOld
import com.tradesk.listeners.SingleListCLickListener

class UsersContractTradeAdapter(
    var context: Context,
    var mList: MutableList<DataTradesOld>, val listener: SingleListCLickListener
) : RecyclerView.Adapter<UsersContractTradeAdapter.VHolder>(){


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_contract_trade, parent, false)
        )
    }

    override fun getItemCount(): Int = mList.size

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            mTvName.setText(mList[position].name.toString().capitalize())
            if (mList[position].isChecked){
                mTvName.setTextColor(context.getColor(R.color.colorAccent))
                mTvDivider.setBackgroundColor(context.getColor(R.color.colorAccent))
            }else{
                mTvName.setTextColor(context.getColor(R.color.black_color))
                mTvDivider.setBackgroundColor(context.getColor(R.color.white_color))
            }

            mTvName.setOnClickListener {

                if (mList[position].isChecked){

                }else{
                    for (i in 0 until mList.size) {
                            mList[i].isChecked=false
                    }
                    mList[position].isChecked=true
                    notifyDataSetChanged()
                }

                listener.onSingleListClick("Click", position)

//                if (!mList[position].checked){
//                    mTvName.setTextColor(context.getColor(R.color.colorAccent))
//                    mTvDivider.setBackgroundColor(context.getColor(R.color.colorAccent))
//
//                }else{
//                    mTvName.setTextColor(context.getColor(R.color.black_color))
//                    mTvDivider.setBackgroundColor(context.getColor(R.color.white_color))
//                }

            }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val mTvName = view.findViewById<TextView>(R.id.mTvName)
        val mTvDivider = view.findViewById<View>(R.id.mTvDivider)


    }


}