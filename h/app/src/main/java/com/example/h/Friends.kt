package com.example.h

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.data.Friend
import com.example.h.data.User
import com.example.h.dataAdapter.FriendAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.FriendViewModel
import com.example.h.viewModel.UserViewModel

class Friends : Fragment() {

    private lateinit var userViewModel : UserViewModel
    private lateinit var userID : String
    private lateinit var recyclerView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friends, container, false)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userID = SaveSharedPreference.getUserID(requireContext())

        recyclerView = view.findViewById(R.id.recyclerViewFriendFriends)
        fetchData()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        return view
    }

    private fun fetchData() {
        val friendViewModel : FriendViewModel = ViewModelProvider(this).get(FriendViewModel::class.java)
        val userList : ArrayList<User> = arrayListOf()
        val friendList = friendViewModel.getFriendList(userID)

        for (friend in friendList) {
            val friendID = if (friend.requestUserID == userID) friend.receiveUserID else friend.requestUserID
            val user = userViewModel.getUserByID(friendID)!!
            userList.add(user)
        }

        val adapter = FriendAdapter(FriendAdapter.Mode.DELETE)
        adapter.initData(userList)
        recyclerView.adapter = adapter

    }
}