package com.tradesk.appCode.profileModule.gallaryModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.AdditionalImageLeadDetail
import com.tradesk.listeners.SingleListCLickListener
import com.socialgalaxyApp.util.extension.loadWallRound

class GallaryAdapterUpdated(
    context: Context,
    val listener: SingleListCLickListener,
    var mImages: MutableList<AdditionalImageLeadDetail>
) : RecyclerView.Adapter<GallaryAdapterUpdated.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_gallery_updated, parent, false)
        )
    }

    override fun getItemCount(): Int = mImages.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {

            val ext = MimeTypeMap.getFileExtensionFromUrl(mImages[position].image)
            val isDoc = ext.equals("pdf", true) || ext.equals("docx", true) || ext.equals("doc", true)
            mIvPic.isVisible = (isDoc).not()
            ivMenu.isVisible = (isDoc).not()
            tvDocument.isVisible = isDoc
            ivMenuTwo.isVisible = isDoc
            if (isDoc) {
                tvDocument.text=mImages[position].image.substringAfterLast("/").capitalize()
            } else {
                mIvPic.loadWallRound(mImages[position].image)
            }
            
            
            mIvPic.loadWallRound(mImages[position].image)

            mIvPic.setOnClickListener { listener.onSingleListClick("Click", position) }
            tvDocument.setOnClickListener { listener.onSingleListClick("Click", position) }
            ivMenu.setOnClickListener { listener.onSingleListClick(ivMenu, position) }
            ivMenuTwo.setOnClickListener { listener.onSingleListClick(ivMenuTwo, position) }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        //        val clCard=view.findViewById<CardView>(R.id.clCard)
//        val tvAssignDate=view.findViewById<TextView>(R.id.tvAssignDate)
//        val tvDueDate=view.findViewById<TextView>(R.id.tvDueDate)
//        val tvTaskDesc=view.findViewById<TextView>(R.id.tvTaskDesc)
        val ivMenu = view.findViewById<ImageView>(R.id.ivMenu)
        val ivMenuTwo = view.findViewById<ImageView>(R.id.ivMenuTwo)
        val tvDocument = view.findViewById<TextView>(R.id.tvDocument)
        val mIvPic = view.findViewById<ImageView>(R.id.mIvPic)


    }

}