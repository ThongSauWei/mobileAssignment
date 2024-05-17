package com.example.h.dataAdapter

import android.app.ActionBar.LayoutParams
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginStart
import androidx.core.view.setMargins
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.GroupChat
import com.example.h.InnerChat
import com.example.h.R
import com.example.h.data.Group
import com.example.h.data.GroupChatLine
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GroupAdapter : RecyclerView.Adapter <GroupAdapter.GroupHolder>() {

    private var groupList = emptyList<Group>()
    private var lastChatList = emptyList<GroupChatLine>()
    private var unseenMsgList = emptyList<Int>()

    private lateinit var fragmentManager : FragmentManager

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    class GroupHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val constraintLayout : ConstraintLayout = itemView.findViewById(R.id.constraintLayoutFriendHolder)
        val constraintSet = ConstraintSet()

        val imgProfile : ImageView = itemView.findViewById(R.id.imgProfileFriendHolder)
        val tvName : TextView = itemView.findViewById(R.id.tvNameFriendHolder)
        val tvText : TextView = itemView.findViewById(R.id.tvTextFriendHolder)
        val dynamicContainer : CardView = itemView.findViewById(R.id.dynamicFriendHolder)

        val time : TextView = TextView(itemView.context)

        val notification : TextView = TextView(itemView.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.friend_holder, parent, false)

        return GroupHolder(itemView)
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        val currentGroup = groupList[position]
        val lastChat = lastChatList[position]

        holder.tvName.text = currentGroup.groupName
        holder.tvText.text = lastChat.content

        val dateTime = LocalDateTime.parse(lastChat.dateTime, dateTimeFormat)
        val duration = Duration.between(LocalDateTime.now(), dateTime)
        var timeText : String

        if (Math.abs(duration.toDays()) >= 2) {
            timeText = Math.abs(duration.toDays()).toString() + " days ago"
        } else if (Math.abs(duration.toDays()) >= 1) {
            timeText = "Yesterday"
        } else {
            timeText = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")).toString()
        }

        // initialise the textview for time
        holder.time.id = View.generateViewId()
        holder.time.text = timeText
        holder.time.typeface = ResourcesCompat.getFont(holder.time.context, R.font.caveat)
        holder.time.textSize = 14f

        // add the textview into the layout
        holder.constraintLayout.addView(holder.time)

        // clone the applied constraints to the constraintSet
        holder.constraintSet.clone(holder.constraintLayout)

        // apply new constraints to the textview time
        holder.constraintSet.connect(
            holder.time.id,
            ConstraintSet.BOTTOM,
            holder.dynamicContainer.id,
            ConstraintSet.TOP,
            5
        )

        holder.constraintSet.connect(
            holder.time.id,
            ConstraintSet.START,
            holder.dynamicContainer.id,
            ConstraintSet.START
        )

        holder.constraintSet.connect(
            holder.time.id,
            ConstraintSet.END,
            holder.dynamicContainer.id,
            ConstraintSet.END
        )

        // apply the constraints to the layout
        holder.constraintSet.applyTo(holder.constraintLayout)

        val unseenMsg = unseenMsgList[position]

        holder.dynamicContainer.removeAllViews()

        if (unseenMsg > 0) {
            holder.tvText.setTypeface(ResourcesCompat.getFont(holder.tvText.context, R.font.caveat), Typeface.BOLD)
            holder.tvText.setTextColor(Color.BLACK)

            // convert dp to px
            val density = holder.dynamicContainer.context.resources.displayMetrics.density

            // initialise the notification textview
            val layoutParamsTextView = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            layoutParamsTextView.marginStart = (5 * density).toInt()
            holder.notification.layoutParams = layoutParamsTextView
            holder.notification.text = unseenMsg.toString()
            holder.notification.typeface = ResourcesCompat.getFont(holder.notification.context, R.font.caveat)
            holder.notification.textSize = 14f
            holder.notification.setTextColor(Color.WHITE)

            // initialise the cardview for notification
            val layoutParamsCardView = holder.dynamicContainer.layoutParams
            layoutParamsCardView.width = (20 * density).toInt()
            layoutParamsCardView.height = (20 * density).toInt()
            holder.dynamicContainer.layoutParams = layoutParamsCardView
            holder.dynamicContainer.setCardBackgroundColor(Color.rgb(229, 75, 35))
            holder.dynamicContainer.cardElevation = (5 * density)
            holder.dynamicContainer.radius = (10 * density)
            holder.dynamicContainer.addView(holder.notification)
        }

        holder.constraintLayout.setOnClickListener {
            val transaction = fragmentManager.fragments.get(0).activity?.supportFragmentManager?.beginTransaction()
            val fragment = GroupChat()

            val bundle = Bundle()
            bundle.putString("groupID", currentGroup.groupID)
            fragment.arguments = bundle

            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
    }

    fun setLastChatList(lastChatList : List<GroupChatLine>, unseenMsgList : List<Int>) {
        this.lastChatList = lastChatList
        this.unseenMsgList = unseenMsgList

        notifyDataSetChanged()
    }

    fun setGroupList(groupList : List<Group>) {
        this.groupList = groupList

        notifyDataSetChanged()
    }

    fun setFragmentManager(fragmentManager : FragmentManager) {
        this.fragmentManager = fragmentManager
    }
}