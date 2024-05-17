package com.example.h.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.h.R
import com.example.h.data.ChatLine
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InnerChatAdapter(val currentUserID : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var chatLineList = emptyList<ChatLine>()
    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    companion object {
        const val SEND_VIEW = 1
        const val RECEIVE_VIEW = 2
    }

    override fun getItemViewType(position: Int): Int {
        val chatLine = chatLineList[position]

        return if (chatLine.senderID == currentUserID) {
            SEND_VIEW
        } else {
            RECEIVE_VIEW
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == SEND_VIEW) {
            val view = inflater.inflate(R.layout.send_chat_holder, parent, false)
            SendViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.receive_chat_holder, parent, false)
            ReceiveViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return chatLineList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatList = chatLineList[position]

        if (holder is SendViewHolder) {
            holder.tvMessage.text = chatList.content

            val dateTime = LocalDateTime.parse(chatList.dateTime, dateTimeFormat)
            val txtTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString()

            holder.tvTime.text = txtTime

        } else if (holder is ReceiveViewHolder){
            holder.tvMessage.text = chatList.content

            val dateTime = LocalDateTime.parse(chatList.dateTime, dateTimeFormat)
            val txtTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString()

            holder.tvTime.text = txtTime
        }
    }

    class SendViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage : TextView = itemView.findViewById(R.id.tvChatSendChatHolder)
        val tvTime : TextView = itemView.findViewById(R.id.tvTimeSendChatHolder)
        val containerBtn : LinearLayout = itemView.findViewById(R.id.containerBtnViewPostSendChatHolder)
    }

    class ReceiveViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage : TextView = itemView.findViewById(R.id.tvChatReceiveChatHolder)
        val tvTime : TextView = itemView.findViewById(R.id.tvTimeReceiveChatHolder)
        val cardViewBtn : CardView = itemView.findViewById(R.id.cardViewBtnViewPostReceiveChatHolder)
    }

    fun setChatLineList(chatLineList : List<ChatLine>) {
        this.chatLineList = chatLineList
    }
}