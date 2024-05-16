package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.data.Chat
import com.example.h.data.User
import com.example.h.dataAdapter.FriendAdapter
import com.example.h.viewModel.ChatLineViewModel
import com.example.h.viewModel.ChatViewModel
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserViewModel
import kotlinx.coroutines.launch

class OuterChat : Fragment() {

    private lateinit var recyclerView : RecyclerView
    private val adapter = FriendAdapter(FriendAdapter.Mode.CHAT)

    private lateinit var userViewModel : UserViewModel
    private lateinit var profileViewModel : ProfileViewModel
    private lateinit var chatViewModel : ChatViewModel
    private lateinit var chatLineViewModel : ChatLineViewModel

    private var userList : ArrayList<User> = arrayListOf()
    private lateinit var chatList : List<Chat>

    private lateinit var currentUserID : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_outer_chat, container, false)

        (activity as MainActivity).setToolbar(R.layout.toolbar_with_profile)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        chatLineViewModel = ViewModelProvider(this).get(ChatLineViewModel::class.java)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerViewFriendOuterChat)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val btnSearch : AppCompatButton = view.findViewById(R.id.btnSearchOuterChat)
        val txtSearch : EditText = view.findViewById(R.id.txtSearchOuterChat)

        setupDefault()

        btnSearch.setOnClickListener {
            val inputText = txtSearch.text.toString()
            searchChat(inputText)
        }

        return view
    }

    private fun setupDefault() {

        lifecycleScope.launch {
            chatList = chatViewModel.getChatByUser(currentUserID)

            chatList.forEach { chat ->
                val userID = if (chat.initiatorUserID == currentUserID) chat.receiverUserID else chat.initiatorUserID
                val user = userViewModel.getUserByID(userID)
                userList.add(user!!)
            }


        }
    }

    private fun searchChat(txtSearch : String) {

    }
}