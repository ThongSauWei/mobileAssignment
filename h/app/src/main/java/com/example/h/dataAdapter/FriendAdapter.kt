package com.example.h.dataAdapter

import android.app.ActionBar
import android.graphics.Color
import android.graphics.ColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.h.R

class FriendAdapter (val mode : Int) : RecyclerView.Adapter <FriendAdapter.FriendHolder>() {

    object Mode {
        const val ADD = 1
        const val DELETE = 2
        const val CHAT = 3
        const val INVITE = 4
    }

    class FriendHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val constraintLayout : ConstraintLayout = itemView.findViewById(R.id.constraintLayoutFriendHolder)
        val constraintSet = ConstraintSet()

        val imgProfile : ImageView = itemView.findViewById(R.id.imgProfileFriendHolder)
        val tvName : TextView = itemView.findViewById(R.id.tvNameFriendHolder)
        val tvText : TextView = itemView.findViewById(R.id.tvTextFriendHolder)
        val dynamicContainer : CardView = itemView.findViewById(R.id.dynamicFriendHolder)

        // for delete & invite
        val imgContent = ImageView(itemView.context)

        // for add
        val btnAdd = Button(itemView.context)

        // for chat
        val time = TextView(itemView.context)
        val notification = TextView(itemView.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.friend_holder, parent, false)

        return FriendHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: FriendHolder, position: Int) {
        holder.tvName.text = "Name " + (position + 1)
        holder.tvText.text = "Business, TARUMT"

        // convert dp to px
        val density = holder.dynamicContainer.context.resources.displayMetrics.density

        // get the default layout settings of the cardview
        val layoutParamsCardView = holder.dynamicContainer.layoutParams

        when (mode) {
            Mode.ADD -> {
                holder.dynamicContainer.radius = (10 * density)

                // initialise the button settings
                holder.btnAdd.text = "Add Buddies"
                holder.btnAdd.typeface = ResourcesCompat.getFont(holder.btnAdd.context, R.font.caveat)
                holder.btnAdd.textSize = 14f
                holder.btnAdd.setTextColor(Color.BLACK)
                holder.btnAdd.isAllCaps = false

                // set the color of the icon and set the icon to the button
                val iconAdd = ContextCompat.getDrawable(holder.btnAdd.context, R.drawable.baseline_person_add_alt_24)?.mutate()
                DrawableCompat.setTint(iconAdd!!, Color.BLACK)
                holder.btnAdd.setCompoundDrawablesRelativeWithIntrinsicBounds(iconAdd, null, null, null)
                holder.btnAdd.compoundDrawablePadding = (8 * density).toInt()
                holder.btnAdd.setPadding((10 * density).toInt(), 0, (10 * density).toInt(), 0)


                // set the background of the button
                holder.btnAdd.setBackgroundResource(R.drawable.button_bg)

                // add the button to the cardview
                holder.dynamicContainer.removeAllViews()
                holder.dynamicContainer.addView(holder.btnAdd)
            }
            Mode.DELETE -> {
                // initialise cardview settings
                layoutParamsCardView.width = (30 * density).toInt()
                layoutParamsCardView.height = (30 * density).toInt()
                holder.dynamicContainer.layoutParams = layoutParamsCardView
                holder.dynamicContainer.cardElevation = (5 * density)

                // set the image inside the cardview
                holder.imgContent.setImageResource(R.drawable.remove_friend)

                // add the image into the cardview
                holder.dynamicContainer.removeAllViews()
                holder.dynamicContainer.addView(holder.imgContent)
            }
            Mode.CHAT -> {
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

                // initialise the notification textview
                val layoutParamsTextView = ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.WRAP_CONTENT
                )
                layoutParamsTextView.marginStart = (5 * density).toInt()
                holder.notification.layoutParams = layoutParamsTextView
                holder.notification.text = "2"
                holder.notification.typeface = ResourcesCompat.getFont(holder.notification.context, R.font.caveat)
                holder.notification.textSize = 14f
                holder.notification.setTextColor(Color.WHITE)

                // initialise the cardview for notification
                layoutParamsCardView.width = (20 * density).toInt()
                layoutParamsCardView.height = (20 * density).toInt()
                holder.dynamicContainer.layoutParams = layoutParamsCardView
                holder.dynamicContainer.setCardBackgroundColor(Color.rgb(229, 75, 35))
                holder.dynamicContainer.cardElevation = (5 * density)
                holder.dynamicContainer.radius = (10 * density)

                // add the notification into the cardview
                holder.dynamicContainer.removeAllViews()
                holder.dynamicContainer.addView(holder.notification)
            }
            Mode.INVITE -> {
                // initialise cardview settings
                layoutParamsCardView.width = (30 * density).toInt()
                layoutParamsCardView.height = (30 * density).toInt()
                holder.dynamicContainer.layoutParams = layoutParamsCardView
                holder.dynamicContainer.setCardBackgroundColor(holder.dynamicContainer.context.getColor(R.color.button))
                holder.dynamicContainer.cardElevation = (5 * density)
                holder.dynamicContainer.radius = (15 * density)

                // set the image inside the cardview
                holder.imgContent.setImageResource(R.drawable.baseline_add_24)

                // add the image into the cardview
                holder.dynamicContainer.removeAllViews()
                holder.dynamicContainer.addView(holder.imgContent)
            }
            else -> {

            }
        }
    }
}