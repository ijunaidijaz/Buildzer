package com.tradesk.appCode.profileModule.gallaryModule.subGallaryModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.*
import com.tradesk.listeners.SingleListCLickListener
import com.socialgalaxyApp.util.extension.loadWallRound

class SubAdditionalGallaryAdapter(
    context: Context,var title:String,
    val listener: SingleListCLickListener,
    var mTasksDataFiltered: MutableList<AdditionalImageImageClient>
) : RecyclerView.Adapter<SubAdditionalGallaryAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_sub_additional_gallery_updated, parent, false)
        )
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
                for (i in 0 until mTasksDataFiltered.size) {
                  if (mTasksDataFiltered[position].status.equals("image")){
                      mIvPic.loadWallRound(mTasksDataFiltered[position].image)
                      break
                  }

                }

            mTitle.visibility=View.GONE
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

}