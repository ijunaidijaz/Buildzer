package com.tradesk.appCode.home.leadsDetail.leadsNotesModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.Note
import com.tradesk.listeners.SingleItemCLickListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class LeadsNotesAdapter(
    context: Context,
    val listener: SingleItemCLickListener,
    var mNotesFiltered: MutableList<Note>
) : RecyclerView.Adapter<LeadsNotesAdapter.VHolder>(),
    Filterable {


    var visiblePos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_noteslist, parent, false)
        )
    }

    override fun getItemCount(): Int = mNotesFiltered.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.apply {
            mTvNoteTitle.text = mNotesFiltered[position].title
            mTvNoteDesc.text = mNotesFiltered[position].description
            mTvDate.text = convertDateFormat(mNotesFiltered[position].createdAt)
            mTvTime.text = convertTimeDateFormat(mNotesFiltered[position].createdAt)
//            tvMarkComplete.text=if (mTasksDataFiltered[position].task_status.equals("Task Completed")) mTasksDataFiltered[position].task_status else "Mark As Complete"
////            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context,if(mTasksDataFiltered[position].task_status.equals("Mark As Complete")) R.color.blue_color else if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.pink_color))
//            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context, if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.blue_color))
//
//            tvMarkComplete.setOnClickListener {
//                if(mTasksDataFiltered[position].task_status.equals("Task Completed").not()){
//                    listener.onSingleListClick(mTasksDataFiltered[position],position)
//                }
            parent.setOnClickListener { listener.onSingleItemClick("1", position) }
        }
    }


    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val parent = view.findViewById<CardView>(R.id.parent)
        val mTvNoteTitle = view.findViewById<TextView>(R.id.mTvNoteTitle)
        val mTvNoteDesc = view.findViewById<TextView>(R.id.mTvNoteDesc)
        val mTvDate = view.findViewById<TextView>(R.id.mTvDate)
        val mTvTime = view.findViewById<TextView>(R.id.mTvTime)
//        val tvMarkComplete=view.findViewById<TextView>(R.id.tvMarkComplete)


    }

    private fun convertDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun convertTimeDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("hh:mm a", Locale.US)
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            var result = FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()) {
//                    mTasksDataFiltered=mTasksData
                    result.count = mNotesFiltered.size
                    result.values = mNotesFiltered
                    return result
                } else {
                    val listing = mutableListOf<Note>()
                    mNotesFiltered.forEach {
                        if (it.title.lowercase(Locale.ENGLISH)
                                .contains(p0.toString().lowercase(Locale.ENGLISH))
                        ) {
                            listing.add(it)
                        }
                    }

                    result.count = listing.size
                    result.values = listing
                    return result
                }
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                mNotesFiltered = p1!!.values as MutableList<Note>
                notifyDataSetChanged()
            }
        }
    }
}