package com.tradesk.appCode.home.addLeadsModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.Client
import com.tradesk.listeners.AddClientListener
import com.tradesk.utils.extension.loadWallImage
import de.hdodenhof.circleimageview.CircleImageView

class AddClientAdapter(
    context: Context,
    var mClientListFiltered: MutableList<Client>,
    var mClientListOriginal: MutableList<Client>,
    var listenerAdd: AddClientListener
) : RecyclerView.Adapter<AddClientAdapter.VHolder>(),
    Filterable {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.add_client_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int =  mClientListFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            if (mClientListFiltered[position].image.isNotEmpty()) {
                mCvUserProfile.loadWallImage(mClientListFiltered[position].image)
            }
            mTvName.setText(mClientListFiltered[position].name)
            cancel.setOnClickListener {
                listenerAdd.onAddClientClick(mClientListFiltered[position],position)
            }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var mCvUserProfile:CircleImageView=view.findViewById(R.id.circle_image_view)
        var mTvName=view.findViewById<TextView>(R.id.name)
        var cancel=view.findViewById<ImageView>(R.id.cross)


    }

//    override fun getFilter(): Filter {
//        return object : Filter(){
//            var result= FilterResults()
//            override fun performFiltering(p0: CharSequence?): FilterResults {
//                if (p0!!.isEmpty()){
////                    mClientListFiltered=mTasksData
//                    result.count=mClientListOriginal.size
//                    result.values=mClientListOriginal
//                    return result
//                }else{
//                    val listing= mutableListOf<Client>()
//                    mClientListFiltered.forEach {
//                        if (it.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))||
//                            it.trade.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))){
//                            listing.add(it)
//                        }
//                    }
//
//                    result.count=listing.size
//                    result.values=listing
//                    return result
//                }
//            }
//
//            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
//                mClientListFiltered= p1!!.values as MutableList<Client>
//                notifyDataSetChanged()
//            }
//        }
//    }


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

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }

}