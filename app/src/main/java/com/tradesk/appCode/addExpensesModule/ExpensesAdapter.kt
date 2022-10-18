package com.tradesk.appCode.addExpensesModule

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
import com.tradesk.data.entity.Expenses
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.extension.loadWallImage
import com.makeramen.roundedimageview.RoundedImageView
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ExpensesAdapter(
    context: Context,
    var mExpensesList: MutableList<Expenses>, val listener: SingleListCLickListener
) : RecyclerView.Adapter<ExpensesAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_expenses, parent, false)
        )
    }

    override fun getItemCount(): Int = mExpensesList.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            mTvClientName.text=mExpensesList[position].title
            mTvDate.text=convertDateFormat(mExpensesList[position].createdAt)

            val formatter = DecimalFormat("#,###,###")
            mTvTotal.setText("$ "+formatter.format(mExpensesList[position].amount.toInt()))

            if (mExpensesList.get(position).image.isNotEmpty()){
                mIvImage.loadWallImage(mExpensesList[position].image)
            }
//            mTvTotal.text="$ "+mExpensesList[position].total



            mIvImage.setOnClickListener {
                listener.onSingleListClick("Image", position)
            }

            mIvDelete.setOnClickListener {
                listener.onSingleListClick("Delete", position)
            }
            view.setOnClickListener {
                listener.onSingleListClick(mExpensesList[position], position)
            }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mTvClientName=view.findViewById<TextView>(R.id.mTvClientName)
        val mTvTotal=view.findViewById<TextView>(R.id.mTvTotal)
        val mTvDate=view.findViewById<TextView>(R.id.mTvDate)
        val maincard=view.findViewById<CardView>(R.id.maincard)
        val mIvDelete=view.findViewById<ImageView>(R.id.mIvDelete)
        val mIvImage=view.findViewById<RoundedImageView>(R.id.imageView13)
    }

    private fun convertDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("MMM dd, yyyy hh:mm", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

}