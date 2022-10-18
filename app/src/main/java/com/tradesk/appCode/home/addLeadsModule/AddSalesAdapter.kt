package com.tradesk.appCode.home.addLeadsModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.Client
import com.tradesk.listeners.AddSalesListener
import com.tradesk.utils.extension.loadWallImage

class AddSalesAdapter(
    context: Context,
    var mClientListFiltered: MutableList<Client>,
    var mClientListOriginal: MutableList<Client>,
    var listenerAdd: AddSalesListener
) : RecyclerView.Adapter<AddSalesAdapter.VHolder>(),
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
                listenerAdd.onAddSalesClick(mClientListFiltered[position],position)
            }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mCvUserProfile:de.hdodenhof.circleimageview.CircleImageView=view.findViewById(R.id.circle_image_view)
        var mTvName=view.findViewById<TextView>(R.id.name)
        var cancel=view.findViewById<ImageView>(R.id.cross)


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

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }

}