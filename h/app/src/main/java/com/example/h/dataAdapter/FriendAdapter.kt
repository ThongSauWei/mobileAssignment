package com.example.h.dataAdapter

import android.app.ActionBar
import android.graphics.Color
import android.os.Bundle
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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.h.FriendProfile
import com.example.h.dialog.DeleteFriendDialog
import com.example.h.R
import com.example.h.SearchFriend
import com.example.h.data.Friend
import com.example.h.data.Profile
import com.example.h.data.User
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.FriendViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FriendAdapter (val mode : Int) : RecyclerView.Adapter <FriendAdapter.FriendHolder>() {

    private lateinit var storageRef : StorageReference
    private lateinit var fragmentManager : FragmentManager
    private lateinit var deleteFriendDialog : DeleteFriendDialog
    private lateinit var friendViewModel : FriendViewModel

    private var friendList = emptyList<Friend>()
    private var userList = emptyList<User>()
    private var profileList = emptyList<Profile>()

    private lateinit var currentUserID : String

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

        // for delete & invite & add
        val imgContent = ImageView(itemView.context)

        // for chat
        val time = TextView(itemView.context)
        val notification = TextView(itemView.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.friend_holder, parent, false)

        storageRef = FirebaseStorage.getInstance().getReference()
        currentUserID = SaveSharedPreference.getUserID(parent.context)

        return FriendHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: FriendHolder, position: Int) {
        val currentUser = userList[position]
        val currentProfile = profileList[position]

        // get the image of the friend
        val ref = storageRef.child("imageProfile").child(currentUser.userID + ".png")
        ref.downloadUrl
            .addOnCompleteListener {
                Glide.with(holder.imgProfile).load(it.result.toString()).into(holder.imgProfile)
            }
        
        holder.tvName.text = currentUser.username
        holder.tvText.text = currentProfile.userCourse

        // convert dp to px
        val density = holder.dynamicContainer.context.resources.displayMetrics.density

        // get the default layout settings of the cardview
        val layoutParamsCardView = holder.dynamicContainer.layoutParams

        when (mode) {
            Mode.ADD -> {
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

                holder.imgContent.setOnClickListener {

                    val newFriend = Friend("0", currentUserID, currentUser.userID, "Pending")

                    friendViewModel.addFriend(newFriend)

                    holder.imgContent.setImageResource(R.drawable.baseline_check_24)
                }

                holder.constraintLayout.setOnClickListener {
                    val transaction = fragmentManager.fragments.get(0).activity?.supportFragmentManager?.beginTransaction()
                    val fragment = FriendProfile()

                    val bundle = Bundle()
                    bundle.putString("friendUserID", currentUser.userID)
                    fragment.arguments = bundle

                    transaction?.replace(R.id.fragmentContainerView, fragment)
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
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

                holder.imgContent.setOnClickListener {
                    val friendID = friendList[position].friendID
                    deleteFriendDialog.friendID = friendID
                    val username = currentUser.username
                    deleteFriendDialog.username = username
                    deleteFriendDialog.viewModel = friendViewModel

                    deleteFriendDialog.show(fragmentManager, "DeleteFriendDialog")
                }

                holder.constraintLayout.setOnClickListener {
                    val transaction = fragmentManager.fragments.get(0).activity?.supportFragmentManager?.beginTransaction()
                    val fragment = FriendProfile()

                    val bundle = Bundle()
                    bundle.putString("friendUserID", currentUser.userID)
                    fragment.arguments = bundle

                    transaction?.replace(R.id.fragmentContainerView, fragment)
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
            }
            Mode.CHAT -> {
                // initialise the textview for time
                holder.time.id = View.generateViewId()
                holder.time.text = "11:50 PM" // dynamic
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
                holder.notification.text = "2" // dynamic
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

    fun setUserList(userList : List<User>, profileList : List<Profile>) {
        this.userList = userList
        this.profileList = profileList

        notifyDataSetChanged()
    }

    fun setFriendList(friendList : List<Friend>) {
        this.friendList = friendList
    }

    fun setViewModel(friendViewModel : FriendViewModel) {
        this.friendViewModel = friendViewModel
    }

    fun setDeleteFriendDialog(deleteFriendDialog : DeleteFriendDialog, fragmentManager : FragmentManager) {
        this.deleteFriendDialog = deleteFriendDialog
        this.fragmentManager = fragmentManager

        notifyDataSetChanged()
    }
}