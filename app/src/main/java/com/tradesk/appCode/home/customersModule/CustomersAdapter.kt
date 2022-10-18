package com.tradesk.appCode.home.customersModule

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.BuildzerApp
import com.tradesk.R
import com.tradesk.data.entity.Client
import com.tradesk.listeners.SingleListCLickListener
import java.util.*

class CustomersAdapter(
    context: Context,
    var mClientsList: MutableList<Client>,
    var mClientsListOriginal: MutableList<Client>,
    val listener: SingleListCLickListener
) : RecyclerView.Adapter<CustomersAdapter.VHolder>(), Filterable {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_customerslist, parent, false)
        )
    }

    override fun getItemCount(): Int = mClientsList.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {

            mTvName.text = mClientsList[position].name
            mTvEmail.text = mClientsList[position].email
            mTvPhone.text = insertString(mClientsList[position].phone_no, "", 0)
            mTvPhone.text = insertString(mTvPhone.text.toString(), ")", 2)
            mTvPhone.text = insertString(mTvPhone.text.toString(), " ", 3)
            mTvPhone.text = insertString(mTvPhone.text.toString(), "-", 7)
            mTvPhone.text = "(" + mTvPhone.text.toString()
            mTvAddress.text =
                mClientsList[position].address.city + ", " + mClientsList[position].address.state
//            tvMarkComplete.setOnClickListener {
//                if(mTasksDataFiltered[position].task_status.equals("Task Completed").not()){
//                    listener.onSingleListClick(mTasksDataFiltered[position],position)
//                }

            clMain.setOnClickListener { listener.onSingleListClick("Edit", position) }
            clEdit.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse("smsto:" + "+1 " + mClientsList[position].phone_no)
                intent.putExtra("sms_body", "")
                BuildzerApp.appContext.startActivity(intent)
            }
            textView7.setOnClickListener {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                dialIntent.data = Uri.parse("tel:" + "+1 " + mClientsList[position].phone_no)
               BuildzerApp.appContext. startActivity(dialIntent)
            }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val clMain = view.findViewById<ConstraintLayout>(R.id.clMain)
        val mTvName = view.findViewById<TextView>(R.id.mTvName)
        val mTvPhone = view.findViewById<TextView>(R.id.mTvPhone)
        val mTvEmail = view.findViewById<TextView>(R.id.mTvEmail)
        val mTvAddress = view.findViewById<TextView>(R.id.mTvAddress)
        val tvStatus = view.findViewById<ImageView>(R.id.tvStatus)
        val textView7 = view.findViewById<ConstraintLayout>(R.id.textView7)
        val clEdit = view.findViewById<ConstraintLayout>(R.id.clEdit)

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            var result = FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()) {
//                    mTasksDataFiltered=mTasksData
                    result.count = mClientsListOriginal.size
                    result.values = mClientsListOriginal
                    return result
                } else {
                    val listing = mutableListOf<Client>()
                    mClientsListOriginal.forEach {
                        if (it.name.lowercase(Locale.ENGLISH)
                                .contains(p0.toString().lowercase(Locale.ENGLISH))
                        ) {
                            listing.add(it)
                        }
                    }

                    result.count = listing.size
                    result.values = listing
                    return result
                }
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                mClientsList = p1!!.values as MutableList<Client>
                notifyDataSetChanged()
            }
        }
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