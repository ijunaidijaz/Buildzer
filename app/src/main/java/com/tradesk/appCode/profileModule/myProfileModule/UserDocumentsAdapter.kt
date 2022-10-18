package com.tradesk.appCode.profileModule.myProfileModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.listeners.SingleItemCLickListener
import com.socialgalaxyApp.util.extension.loadWallRound

class UserDocumentsAdapter(
    context: Context,
    val listener: SingleItemCLickListener,
    var mDocs: MutableList<String>,
) : RecyclerView.Adapter<UserDocumentsAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_documents, parent, false)
        )
    }

    override fun getItemCount(): Int = mDocs.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            val ext = MimeTypeMap.getFileExtensionFromUrl(mDocs[position])
            val isDoc = ext.equals("pdf", true) || ext.equals("docx", true) || ext.equals("doc", true)
            mIvDocs.isVisible = (isDoc).not()
            tvDocument.isVisible = isDoc
            if (isDoc) {
                tvDocument.text=mDocs[position].substringAfterLast("/")
            } else {
                mIvDocs.loadWallRound(mDocs[position])
            }
            clParent.setOnClickListener { listener.onSingleItemClick("detail", position) }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val clParent = view.findViewById<ConstraintLayout>(R.id.clParent)
        val mIvDocs = view.findViewById<ImageView>(R.id.mIvDocs)
        val tvDocument = view.findViewById<TextView>(R.id.tvDocument)


    }

}