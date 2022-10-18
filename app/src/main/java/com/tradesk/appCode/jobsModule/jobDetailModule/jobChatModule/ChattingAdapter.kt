package com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.data.entity.AddConversationData
import com.tradesk.utils.extension.loadImageRadius
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ChattingAdapter(
    val context: Context, val id: String, val chatList: ArrayList<AddConversationData>,
    val onClick: (String, Int) -> Unit,
    /*var chatting_list: MutableList<MessageChat>,*/
//    val listener: SingleListCLickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TEXT = 0
    val IMAGE = 1

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].message.text.isNotEmpty()) TEXT else IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TEXT)
            VHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_item_messages, parent, false)
            ) else ImageHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_messages_image, parent, false)
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val isMine = chatList[position].sender != null && chatList[position].sender == id

        when (holder) {
            is VHolder -> {
                holder.relative_left.isVisible = isMine.not()
                holder.relative_right_Message.isVisible = isMine
                if (isMine) {
                    holder.right_text.text = chatList[position].message.text
                    holder.right_message_time.text =
                        chatList[position].createdAt.getFormatTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                } else {
                    holder.left_text.text = chatList[position].message.text
                    holder.left_message_time.text =
                        chatList[position].createdAt.getFormatTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                }

                holder.view.setOnClickListener {
                    onClick("hide", position)
                }
            }
            is ImageHolder -> {
                holder.llLeft.isVisible = isMine.not()
                holder.llRight.isVisible = isMine
                holder.left_message_time.text =
                    chatList[position].createdAt.getFormatTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")//2022-03-21T13:04:08.300Z
                holder.right_message_time.text =
                    chatList[position].createdAt.getFormatTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                if (isMine)
                    holder.ivRight.loadImageRadius(chatList[position].message.image)
                else {
                    holder.ivLeft.loadImageRadius(chatList[position].message.image)
                }
                holder.view.setOnClickListener {
                    onClick("hide", position)
                }
                holder.llRight.setOnClickListener {
                    onClick("image", position)
                }
                holder.llLeft.setOnClickListener {
                    onClick("image", position)
                }
            }
        }
    }

    fun String.getFormatTime(from: String): String {
        var sdf = SimpleDateFormat(from, Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        try {
            val date = sdf.parse(this)
            val cal = Calendar.getInstance().also { it.time = date }
            val min = TimeUnit.MILLISECONDS.toMinutes(cal.timeInMillis)
            val hrs = TimeUnit.MILLISECONDS.toHours(cal.timeInMillis)
            return if (hrs < 1 && min < 1) {
                "Just now"
            } else if (hrs < 1) {
                "$min min"
            } else {
                if (hrs <= 24) {
                    "$hrs h"
                } else {
                    sdf.timeZone = TimeZone.getDefault()
                    sdf = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH)
                    return sdf.format(date)
                }
            }
        } catch (e1: ParseException) {
            e1.printStackTrace()
        }
        return ""
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    class VHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val relative_left = itemView.findViewById<RelativeLayout>(R.id.relative_left)
        val relative_right_Message =
            itemView.findViewById<RelativeLayout>(R.id.relative_right_Message)
        val left_text = itemView.findViewById<TextView>(R.id.left_text)
        val right_text = itemView.findViewById<TextView>(R.id.right_text)
        val right_message_time = itemView.findViewById<TextView>(R.id.right_message_time)
        val left_message_time = itemView.findViewById<TextView>(R.id.left_message_time)


    }

    class ImageHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val llLeft = itemView.findViewById<LinearLayout>(R.id.llLeft)
        val ivLeft = itemView.findViewById<ImageView>(R.id.ivLeft)
        val left_message_time = itemView.findViewById<TextView>(R.id.left_message_time)
        val ivRight = itemView.findViewById<ImageView>(R.id.ivRight)
        val right_message_time = itemView.findViewById<TextView>(R.id.right_message_time)
        val llRight = itemView.findViewById<LinearLayout>(R.id.llRight)


    }


}