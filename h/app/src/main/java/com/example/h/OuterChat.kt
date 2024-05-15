package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.data.Profile
import com.example.h.data.User
import com.example.h.dataAdapter.FriendAdapter
import com.example.h.viewModel.ChatLineViewModel
import com.example.h.viewModel.ChatViewModel
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserViewModel

class OuterChat : Fragment() {

    private lateinit var recyclerView : RecyclerView
    private val adapter = FriendAdapter(FriendAdapter.Mode.INVITE)

    private lateinit var userViewModel : UserViewModel
    private lateinit var profileViewModel : ProfileViewModel
    private lateinit var chatViewModel : ChatViewModel
    private lateinit var chatLineViewModel : ChatLineViewModel

    private var userList : ArrayList<User> = arrayListOf()
    private val profileList : ArrayList<Profile> = arrayListOf()

    private lateinit var currentUserID : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_outer_chat, container, false)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        chatLineViewModel = ViewModelProvider(this).get(ChatLineViewModel::class.java)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerViewFriendOuterChat)
        recyclerView.adapter = FriendAdapter(FriendAdapter.Mode.CHAT)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        return view
    }
}