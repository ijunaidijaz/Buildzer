package com.tradesk.appCode.profileModule.usersContModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.Client
import com.tradesk.listeners.SingleListCLickListener
import com.tradesk.utils.extension.loadWallImage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class UsersContractAdapter(
    context: Context,
    var mClientListFiltered: MutableList<Client>,
    var mClientListOriginal: MutableList<Client>,
    val listener: SingleListCLickListener
) : RecyclerView.Adapter<UsersContractAdapter.VHolder>(),
    Filterable {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_userscontract, parent, false)
        )
    }

    override fun getItemCount(): Int =  mClientListFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            if (mClientListFiltered[position].image.isNotEmpty()) {
                mCvUserProfile.loadWallImage(mClientListFiltered[position].image)
            }
            mTvName.setText(mClientListFiltered[position].name)
            mTvEmail.setText(mClientListFiltered[position].email)
            mTvAddress.setText(mClientListFiltered[position].address.street+", "+mClientListFiltered[position].address.city)

            if (mClientListFiltered[position].trade.equals("admin")){
                            tvStatusText.setText(" Admin ")
            }else if (mClientListFiltered[position].trade.equals("manager")){
                tvStatusText.setText(" Manager ")
            }else if (mClientListFiltered[position].trade.equals("employee")){
                tvStatusText.setText(" Employee ")
            }else{
                tvStatusText.setText(mClientListFiltered[position].trade.capitalize())
            }


            mTvPhone.setText(insertString(mClientListFiltered[position].phone_no,"",0))
            mTvPhone.setText(insertString(mTvPhone.text.toString(),")",2))
            mTvPhone.setText(insertString(mTvPhone.text.toString()," ",3))
            mTvPhone.setText(insertString(mTvPhone.text.toString(),"-",7))
            mTvPhone.setText("+1 ("+mTvPhone.text.toString())


            tvEdit.setOnClickListener {
                    listener.onSingleListClick("Edit", position)
            }

            main.setOnClickListener {
                listener.onSingleListClick("main", position)
            }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mCvUserProfile=view.findViewById<CircleImageView>(R.id.mCvUserProfile)
        val main=view.findViewById<ConstraintLayout>(R.id.main)
        val mTvName=view.findViewById<TextView>(R.id.mTvUserName)
        val tvStatusText=view.findViewById<TextView>(R.id.mTvUserStatus)
        val tvEdit=view.findViewById<ImageView>(R.id.mTvUserEdit)
        val mTvPhone=view.findViewById<TextView>(R.id.mTvUserPhone)
        val mTvEmail=view.findViewById<TextView>(R.id.mTvUserEmail)
        val mTvAddress=view.findViewById<TextView>(R.id.mTvUserAddress)


    }

    override fun getFilter(): Filter {
        return object : Filter(){
            var result= FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()){
//                    mClientListFiltered=mTasksData
                    result.count=mClientListOriginal.size
                    result.values=mClientListOriginal
                    return result
                }else{
                    val listing= mutableListOf<Client>()
                    mClientListOriginal.forEach {
                        if (it.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))||
                            it.trade.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
                            || it.designation.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))){
                            listing.add(it)
                        }
                    }

                    result.count=listing.size
                    result.values=listing
                    return result
                }
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                mClientListFiltered= p1!!.values as MutableList<Client>
                notifyDataSetChanged()
            }
        }
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