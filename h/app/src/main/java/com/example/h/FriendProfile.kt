package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
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

        val friendUserID = arguments?.getString("friendUserID")!!

        friendViewModel = ViewModelProvider(this).get(FriendViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerViewPostFriendProfile)
        profileSetup(friendUserID)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val btnAdd : AppCompatButton = view.findViewById(R.id.btnAddFriendFriendProfile)
        val btnMessage : AppCompatButton = view.findViewById(R.id.btnMessageFriendProfile)

        btnAdd.setOnClickListener {
            val friendID = "0"
            val userID = SaveSharedPreference.getUserID(requireContext())
            val status = "Pending"

            val friend = Friend(friendID, userID, friendUserID, status)

            friendViewModel.addFriend(friend)
        }

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

            val adapter = PostAdapter(postList)
            recyclerView.adapter = adapter
        }
    }
}