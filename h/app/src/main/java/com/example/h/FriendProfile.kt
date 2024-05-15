package com.example.h

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.h.data.Friend
import com.example.h.data.Post
import com.example.h.dataAdapter.PostAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.FriendViewModel
import com.example.h.viewModel.PostViewModel
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserGroupViewModel
import com.example.h.viewModel.UserViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch

class FriendProfile : Fragment() {
    private val storageRef : StorageReference = FirebaseStorage.getInstance().getReference()

    private lateinit var friendViewModel : FriendViewModel

    private lateinit var imgProfile : ImageView
    private lateinit var tvName : TextView
    private lateinit var tvGroup : TextView
    private lateinit var tvFriend : TextView
    private lateinit var tvDOB : TextView
    private lateinit var tvCourse : TextView
    private lateinit var tvBio : TextView
    private lateinit var separator : View
    private lateinit var btnAdd : AppCompatButton
    private lateinit var layout : ConstraintLayout

    private lateinit var currentUserID : String

    private lateinit var postList : List<Post>

    private lateinit var recyclerView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friend_profile, container, false)

        if (arguments?.getString("friendUserID").isNullOrEmpty()) {

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = SearchFriend()

            val bundle = Bundle()
            bundle.putString("NoSuchFriend", "No such friend")
            fragment.arguments = bundle

            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()

        }

        currentUserID = SaveSharedPreference.getUserID(requireContext())

        imgProfile = view.findViewById(R.id.imgProfileFriendProfile)
        tvName = view.findViewById(R.id.tvNameFriendProfile)
        tvGroup = view.findViewById(R.id.tvGroupsFriendProfile)
        tvFriend = view.findViewById(R.id.tvFriendsFriendProfile)
        tvDOB = view.findViewById(R.id.tvDOBFriendProfile)
        tvCourse = view.findViewById(R.id.tvCoursesFriendProfile)
        tvBio = view.findViewById(R.id.tvBioFriendProfile)
        separator = view.findViewById(R.id.separatorFriendProfile)

        val friendUserID = arguments?.getString("friendUserID")!!

        friendViewModel = ViewModelProvider(this).get(FriendViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerViewPostFriendProfile)
        profileSetup(friendUserID)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        btnAdd = view.findViewById(R.id.btnAddFriendFriendProfile)
        val btnMessage : AppCompatButton = view.findViewById(R.id.btnMessageFriendProfile)

        layout = view.findViewById(R.id.layoutFriendProfile)

        btnMessage.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = InnerChat()

            val bundle = Bundle()
            bundle.putString("friendUserID", friendUserID)
            fragment.arguments = bundle

            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }

    private fun profileSetup(userID : String) {
        val userViewModel : UserViewModel =
            ViewModelProvider(this).get(UserViewModel::class.java)
        val profileViewModel : ProfileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val userGroupViewModel : UserGroupViewModel =
            ViewModelProvider(this).get(UserGroupViewModel::class.java)
        val postViewModel : PostViewModel =
            ViewModelProvider(this).get(PostViewModel::class.java)

        lifecycleScope.launch {

            val friend = userViewModel.getUserByID(userID)!!
            val profile = profileViewModel.getProfile(userID)!!
            val totalGroup = userGroupViewModel.getUserGroupByUser(userID).size
            val totalFriend = friendViewModel.getFriendList(userID).size
            postList = postViewModel.getPostByUser(userID)

            val ref = storageRef.child("imageProfile").child(friend.userID + ".png")
            ref.downloadUrl
                .addOnCompleteListener {
                    Glide.with(imgProfile).load(it.result.toString()).into(imgProfile)
                }

            tvName.text = friend.username
            tvGroup.text = totalGroup.toString()
            tvFriend.text = totalFriend.toString()
            tvDOB.text = friend.userDOB
            tvCourse.text = profile.userCourse
            tvBio.text = profile.userBio

            buttonSetup(currentUserID, userID)

            if (postList.size > 0) {
                val adapter = PostAdapter(postList)
                recyclerView.adapter = adapter

            } else {
                noPostSetup()
            }
        }
    }

    private fun buttonSetup(userID : String, friendUserID : String) {
        lifecycleScope.launch {
            val friend = friendViewModel.getFriend(userID, friendUserID)

            var onClickListener : (View) -> Unit = {}

            if (friend != null) {
                when (friend.status) {
                    "Pending", "Blocked", "Friend" -> {
                        if (friend.status == "Pending" && friend.receiveUserID == currentUserID) {
                            btnAdd.text = "Accept"

                            onClickListener = {
                                val updatedFriend = Friend(friend.friendID, friend.requestUserID, friend.receiveUserID, "Friend")
                                friendViewModel.updateFriend(updatedFriend)
                            }

                        } else {
                            btnAdd.text = friend.status
                            btnAdd.setBackgroundColor(requireContext().getColor(R.color.disabled_button))
                            btnAdd.isClickable = false
                        }
                    }
                    "Blocking" -> {
                        btnAdd.text = "Unblock"

                        onClickListener = {
                            val updatedFriend = Friend(friend.friendID, friend.requestUserID, friend.receiveUserID, "Friend")
                            friendViewModel.updateFriend(updatedFriend)
                        }
                    }
                    else -> {

                    }
                }
            } else {
                onClickListener = {
                    val newFriend = Friend("0", userID, friendUserID, "Pending")

                    friendViewModel.addFriend(newFriend)
                }
            }

            btnAdd.setOnClickListener(onClickListener)
        }
    }


    private fun noPostSetup() {
        val tvNoPost = TextView(requireContext())
        tvNoPost.id = View.generateViewId()
        tvNoPost.text = "User has No Post"
        tvNoPost.typeface = ResourcesCompat.getFont(requireContext(), R.font.caveat)
        tvNoPost.setTextColor(requireContext().getColor(R.color.sub_text))
        tvNoPost.textSize = 30f

        layout.addView(tvNoPost)

        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        constraintSet.connect(
            tvNoPost.id,
            ConstraintSet.TOP,
            separator.id,
            ConstraintSet.BOTTOM,
            150
        )

       constraintSet.connect(
            tvNoPost.id,
            ConstraintSet.START,
            layout.id,
            ConstraintSet.START
        )

        constraintSet.connect(
            tvNoPost.id,
            ConstraintSet.END,
            layout.id,
            ConstraintSet.END
        )

        // apply the constraints to the layout
        constraintSet.applyTo(layout)
    }
}