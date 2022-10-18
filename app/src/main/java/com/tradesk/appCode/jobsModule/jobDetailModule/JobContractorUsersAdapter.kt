package com.tradesk.appCode.jobsModule.jobDetailModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.Sale
import com.tradesk.listeners.SingleItemCLickListener
import com.socialgalaxyApp.util.extension.loadWallRound
import de.hdodenhof.circleimageview.CircleImageView

class JobContractorUsersAdapter(
    context: Context,
    val listener: SingleItemCLickListener,
    var mTasksDataFiltered: MutableList<Sale>
) : RecyclerView.Adapter<JobContractorUsersAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_contractorusers, parent, false)
        )
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            mIvName.text = mTasksDataFiltered[position].name
            if (mTasksDataFiltered[position].image.isNotEmpty()) {
                mIvImage.loadWallRound(mTasksDataFiltered[position].image)
            }
            clParent.setOnClickListener { listener.onSingleItemClick("123", position) }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val clParent = view.findViewById<ConstraintLayout>(R.id.clParent)
        val mIvImage = view.findViewById<CircleImageView>(R.id.mIvImage)
        val mIvName = view.findViewById<TextView>(R.id.mIvName)


    }

}