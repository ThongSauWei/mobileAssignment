package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.data.Friend
import com.example.h.data.Profile
import com.example.h.data.User
import com.example.h.dataAdapter.FriendAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.FriendViewModel
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserViewModel
import kotlinx.coroutines.launch

class Friends : Fragment() {

    private lateinit var userViewModel : UserViewModel
    private lateinit var userID : String
    private lateinit var recyclerView : RecyclerView

    private val userList : ArrayList<User> = arrayListOf()
    private val profileList : ArrayList<Profile> = arrayListOf()
    private lateinit var friendList : List<Friend>

    private lateinit var tvCount : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friends, container, false)

        tvCount = view.findViewById(R.id.tvFriendCountFriends)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userID = SaveSharedPreference.getUserID(requireContext())

        recyclerView = view.findViewById(R.id.recyclerViewFriendFriends)
        fetchData()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        return view
    }

    private fun fetchData() {
        lifecycleScope.launch {

            val friendViewModel : FriendViewModel = ViewModelProvider(this@Friends).get(FriendViewModel::class.java)
            val profileViewModel : ProfileViewModel = ViewModelProvider(this@Friends).get(ProfileViewModel::class.java)

            friendList = friendViewModel.getFriendList(userID)

            for (friend in friendList) {
                val friendID = if (friend.requestUserID == userID) friend.receiveUserID else friend.requestUserID
                val user = userViewModel.getUserByID(friendID)
                userList.add(user!!)
                val userProfile = profileViewModel.getProfile(user.userID)
                profileList.add(userProfile!!)
            }

            val adapter = FriendAdapter(FriendAdapter.Mode.DELETE)
            adapter.initFriend(userList, profileList)
            recyclerView.adapter = adapter

            tvCount.text = userList.size.toString() + " Buddies"
        }
    }
}