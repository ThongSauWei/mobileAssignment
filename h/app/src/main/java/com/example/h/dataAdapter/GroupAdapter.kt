package com.example.h.dataAdapter

import android.app.ActionBar.LayoutParams
import android.graphics.Color
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
import androidx.recyclerview.widget.RecyclerView
import com.example.h.R

class GroupAdapter : RecyclerView.Adapter <GroupAdapter.GroupHolder>() {

    class GroupHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val constraintLayout : ConstraintLayout = itemView.findViewById(R.id.constraintLayoutFriendHolder)
        val constraintSet = ConstraintSet()

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
        return 10
    }

    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        holder.tvName.text = "Group " + (position + 1)
        holder.tvText.text = "Helooooooooooooooooooooooo byebyeeeeeeeeeee"

        // initialise the textview for time
        holder.time.id = View.generateViewId()
        holder.time.text = "11:50 PM"
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

        // convert dp to px
        val density = holder.dynamicContainer.context.resources.displayMetrics.density

        // initialise the notification textview
        val layoutParamsTextView = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParamsTextView.marginStart = (5 * density).toInt()
        holder.notification.layoutParams = layoutParamsTextView
        holder.notification.text = "2"
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
        holder.dynamicContainer.radius = (15 * density)

        holder.dynamicContainer.removeAllViews()
        holder.dynamicContainer.addView(holder.notification)
    }
}