package com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.LeadsData

class ChatUserAdapter(
    val context: Context, val usertype: String, val chatUserList: ArrayList<LeadsData>,

    /*var chatting_list: MutableList<MessageChat>,*/
//    val listener: SingleListCLickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_chat_user, parent, false)
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {

        when (holder) {
            is VHolder -> {
                holder.left_text.text = chatUserList[pos].project_name
                holder.view.setOnClickListener {
                    val receiver_id = if (usertype == "1") {
                        chatUserList[pos].sales[0]._id
                    } else {
                        chatUserList[pos].created_by
                    }
                    context.startActivity(
                        Intent(context, ChatActivity::class.java)
                            .putExtra("job_id", chatUserList[pos]._id)
                            .putExtra("receiver_id", receiver_id)
                            .putExtra("sales_id", chatUserList[pos].sales[0]._id)
                            .putExtra("job_title", chatUserList[pos].project_name)

                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return chatUserList.size
    }

    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val left_text = itemView.findViewById<TextView>(R.id.left_text)


    }


}