package com.tradesk.appCode.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tradesk.BuildzerApp
import com.tradesk.R
import com.tradesk.listeners.AttachedDocListener
import com.tradesk.utils.extension.loadCenterFitImage
import com.tradesk.utils.extension.loadUserImage
import com.tradesk.utils.extension.loadWallImage

class AttachedDocsAdapter(
    context: Context, val listener: AttachedDocListener,
    var mTasksDataFiltered: MutableList<String>
) : RecyclerView.Adapter<AttachedDocsAdapter.VHolder>() {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.attached_doc_list_item, parent, false)
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
            val extension = someFilepath.substring(someFilepath.lastIndexOf("."))
            name.text = nameValue
           if (extension.equals(".jpeg",true)||extension.equals(".jpg",true)||extension.equals(".png",true)) image.loadWallImage(mTasksDataFiltered[position])
            else BuildzerApp.appContext.getDrawable(R.drawable.ic_icon_files)
               ?.let { image.loadUserImage(it) }
            parent.setOnClickListener { listener.onDocClick(mTasksDataFiltered[position], position) }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val parent = view.findViewById<LinearLayout>(R.id.parent)
        val name = view.findViewById<TextView>(R.id.name)
        val image = view.findViewById<ImageView>(R.id.image)
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