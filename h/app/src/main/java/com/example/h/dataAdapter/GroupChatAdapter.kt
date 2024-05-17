package com.example.h.dataAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.h.R
import com.example.h.data.GroupChatLine
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GroupChatAdapter(val currentUserID : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var groupChatLineList = emptyList<GroupChatLine>()
    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private lateinit var storageRef : StorageReference

    companion object {
        const val SEND_VIEW = 1
        const val RECEIVE_VIEW = 2
    }

    override fun getItemViewType(position: Int): Int {
        val chatLine = groupChatLineList[position]

        return if (chatLine.senderID == currentUserID) {
            SEND_VIEW
        } else {
            RECEIVE_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        storageRef = FirebaseStorage.getInstance().getReference()

        return if (viewType == SEND_VIEW) {
            val view = inflater.inflate(R.layout.send_group_chat_holder, parent, false)
            SendViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.receive_group_chat_holder, parent, false)
            ReceiveViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return groupChatLineList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatLine = groupChatLineList[position]

        if (holder is SendViewHolder) {
            holder.tvMessage.text = chatLine.content

            val dateTime = LocalDateTime.parse(chatLine.dateTime, dateTimeFormat)
            val txtTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString()

            holder.tvTime.text = txtTime

            val ref = storageRef.child("imageProfile").child(chatLine.senderID + ".png")
            ref.downloadUrl
                .addOnCompleteListener {
                    Glide.with(holder.imgProfile).load(it.result.toString()).into(holder.imgProfile)
                }

        } else if (holder is ReceiveViewHolder){
            holder.tvMessage.text = chatLine.content

            val dateTime = LocalDateTime.parse(chatLine.dateTime, dateTimeFormat)
            val txtTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString()

            holder.tvTime.text = txtTime

            val ref = storageRef.child("imageProfile").child(chatLine.senderID + ".png")
            ref.downloadUrl
                .addOnCompleteListener {
                    Glide.with(holder.imgProfile).load(it.result.toString()).into(holder.imgProfile)
                }
        }
    }

    class SendViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage : TextView = itemView.findViewById(R.id.tvChatSendGroupChatHolder)
        val tvTime : TextView = itemView.findViewById(R.id.tvTimeSendGroupChatHolder)
        val containerBtn : LinearLayout = itemView.findViewById(R.id.containerBtnViewPostSendGroupChatHolder)
        val imgProfile : ImageView = itemView.findViewById(R.id.imgProfileSendGroupChatHolder)
    }

    class ReceiveViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage : TextView = itemView.findViewById(R.id.tvChatReceiveGroupChatHolder)
        val tvTime : TextView = itemView.findViewById(R.id.tvTimeReceiveGroupChatHolder)
        val cardViewBtn : CardView = itemView.findViewById(R.id.cardViewBtnViewPostReceiveGroupChatHolder)
        val imgProfile : ImageView = itemView.findViewById(R.id.imgProfileReceiveGroupChatHolder)
    }

    fun setGroupChatLineList(groupChatLineList : List<GroupChatLine>) {
        this.groupChatLineList = groupChatLineList

        notifyDataSetChanged()
    }
}