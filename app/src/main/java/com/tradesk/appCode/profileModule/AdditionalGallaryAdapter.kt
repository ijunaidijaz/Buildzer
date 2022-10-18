package com.tradesk.appCode.profileModule

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleListCLickListener
import com.socialgalaxyApp.util.extension.loadWallRound
import java.util.*

class AdditionalGallaryAdapter(
    context: Context,
    val listener: SingleListCLickListener,
    var mTasksDataFiltered: MutableList<LeadsDataImageClient>
) : RecyclerView.Adapter<AdditionalGallaryAdapter.VHolder>(), Filterable {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_additional_gallery_updated, parent, false)
        )
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
                for (i in 0 until mTasksDataFiltered[position].additional_images.size) {
                  if (mTasksDataFiltered[position].additional_images[i].status.equals("image")){
                      mIvPic.loadWallRound(mTasksDataFiltered[position].additional_images[i].image)
                      break
                  }

                }

            mTitle.setText(mTasksDataFiltered[position].project_name.capitalize()+" - "+ mTasksDataFiltered[position].client_details.name)
            mIvPic.setOnClickListener { listener.onSingleListClick("Multi", position) }
            ivMenu.setOnClickListener { listener.onSingleListClick(ivMenu, position) }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        //        val clCard=view.findViewById<CardView>(R.id.clCard)
//        val tvAssignDate=view.findViewById<TextView>(R.id.tvAssignDate)
//        val tvDueDate=view.findViewById<TextView>(R.id.tvDueDate)
//        val tvTaskDesc=view.findViewById<TextView>(R.id.tvTaskDesc)
        val ivMenu = view.findViewById<ImageView>(R.id.ivMenu)
        val mIvPic = view.findViewById<ImageView>(R.id.mIvPic)
        val mTitle = view.findViewById<TextView>(R.id.mTitle)


    }

    override fun getFilter(): Filter {
        return object : Filter(){
            var result= FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()){
//                    mClientListFiltered=mTasksData
                    result.count=mTasksDataFiltered.size
                    result.values=mTasksDataFiltered
                    return result
                }else{
                    val listing= mutableListOf<LeadsDataImageClient>()
                    mTasksDataFiltered.forEach {
                        if (it.project_name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
                            /*|| it.client_details.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))*/){
                            listing.add(it)
                        }
                    }

                    result.count=listing.size
                    result.values=listing
                    return result
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                mTasksDataFiltered= p1!!.values as MutableList<LeadsDataImageClient>
                notifyDataSetChanged()
            }
        }
    }


}