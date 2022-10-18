package com.tradesk.appCode.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.datatransport.runtime.scheduling.jobscheduling.SchedulerConfig
import com.tradesk.BuildzerApp
import com.tradesk.R
import com.tradesk.listeners.AttachedDocListener
import com.tradesk.listeners.DocListener
import com.tradesk.utils.extension.loadCenterFitImage
import com.tradesk.utils.extension.loadUserImage
import com.tradesk.utils.extension.loadWallImage

class DocsAdapter(
    var context: Context,
    val listener: DocListener,
    var mTasksDataFiltered: MutableList<String>
) : RecyclerView.Adapter<DocsAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.docs_item_list, parent, false)
        )
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            var nameValue = mTasksDataFiltered[position].replace(
                "https://tradesk.s3.us-east-2.amazonaws.com/docs/",
                ""
            )
            val someFilepath = mTasksDataFiltered[position]
            name.text = nameValue
            parent.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(someFilepath))
                browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                BuildzerApp.appContext.startActivity(browserIntent)
            }
            cross.setOnClickListener {
                listener.onCrossClick(mTasksDataFiltered[position],position)
            }

        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val parent = view.findViewById<RelativeLayout>(R.id.parent)
        val name = view.findViewById<TextView>(R.id.docName)
        val cross = view.findViewById<ImageView>(R.id.crossDoc)
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