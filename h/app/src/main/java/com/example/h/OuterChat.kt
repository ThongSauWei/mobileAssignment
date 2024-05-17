package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.data.Chat
import com.example.h.data.ChatLine
import com.example.h.data.User
import com.example.h.dataAdapter.FriendAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.ChatLineViewModel
import com.example.h.viewModel.ChatViewModel
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OuterChat : Fragment() {

    private lateinit var recyclerView : RecyclerView
    private val adapter = FriendAdapter(FriendAdapter.Mode.CHAT)

    private lateinit var userViewModel : UserViewModel
    private lateinit var chatViewModel : ChatViewModel
    private lateinit var chatLineViewModel : ChatLineViewModel

    private var userList = mutableListOf<User>()
    private var lastChatList = mutableListOf<ChatLine>()
    private var unseenMsgList : ArrayList<Int> = arrayListOf()

    private lateinit var currentUserID : String

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_outer_chat, container, false)

        (activity as MainActivity).setToolbar(R.layout.toolbar_with_profile)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        chatLineViewModel = ViewModelProvider(this).get(ChatLineViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerViewFriendOuterChat)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        adapter.setFragmentManager(parentFragmentManager)

        currentUserID = SaveSharedPreference.getUserID(requireContext())

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
            val tempChatList = chatViewModel.getChatByUser(currentUserID)

            val chatIDList = ArrayList<String>()
            tempChatList.forEach {
                chatIDList.add(it.chatID)
            }

            chatLineViewModel.lastChatLineList.observe(viewLifecycleOwner, Observer { lastChat ->
                lifecycleScope.launch {
                    if (lastChat != null) {
                        lastChatList.clear()
                        unseenMsgList.clear()
                        userList.clear()

                        lastChatList =
                            lastChat.sortedByDescending { it.dateTime }.toMutableList()

                        lastChatList.forEach { chatLine ->
                            val chat = chatViewModel.getChatByID(chatLine.chatID)!!

                            val userID: String
                            val lastSeen: LocalDateTime

                            if (chat.initiatorUserID == currentUserID) {
                                userID = chat.receiverUserID
                                lastSeen =
                                    LocalDateTime.parse(chat.initiatorLastSeen, dateTimeFormat)
                            } else {
                                userID = chat.initiatorUserID
                                lastSeen =
                                    LocalDateTime.parse(chat.receiverLastSeen, dateTimeFormat)
                            }

                            val user = userViewModel.getUserByID(userID)
                            userList.add(user!!)

                            val chatLineList = chatLineViewModel.getChatLine(chat.chatID).toMutableList()
                            chatLineList.sortByDescending { it.dateTime }

                            var counter = 0
                            for (eachChatLine in chatLineList) {
                                val dateTime = LocalDateTime.parse(
                                    eachChatLine.dateTime,
                                    dateTimeFormat
                                )

                                if (dateTime > lastSeen) {
                                    counter++
                                } else {
                                    break
                                }
                            }
                            unseenMsgList.add(counter)
                        }

                        adapter.setUserList(userList)
                        adapter.setLastChatList(lastChatList, unseenMsgList)
                        recyclerView.adapter = adapter

                        // can add a "No such friend" if no record
                    }
                }
            })

            chatLineViewModel.fetchLastChatList(chatIDList)
        }
    }

    private fun searchChat(txtSearch : String) {
        if (txtSearch.isEmpty()) {
            setupDefault()

        } else {

            userList.clear()

            lifecycleScope.launch {
                userList = ArrayList(userViewModel.searchUser(txtSearch))
                userList.removeIf { it.userID == currentUserID }

                val chatIDList = ArrayList<String>()

                for (user in userList) {
                    val chat = chatViewModel.getChat(currentUserID, user.userID)
                    if (chat != null) {
                        chatIDList.add(chat.chatID)
                    }
                }

                chatLineViewModel.lastChatLineList.observe(viewLifecycleOwner, Observer { lastChat ->
                    lifecycleScope.launch {
                        if (lastChat != null) {
                            lastChatList.clear()
                            unseenMsgList.clear()
                            userList.clear()

                            lastChatList =
                                lastChat.sortedByDescending {it.dateTime }.toMutableList()
                        }

                        lastChatList.forEach { chatLine ->
                            val chat = chatViewModel.getChatByID(chatLine.chatID)!!

                            val userID: String
                            val lastSeen: LocalDateTime

                            if (chat.initiatorUserID == currentUserID) {
                                userID = chat.receiverUserID
                                lastSeen =
                                    LocalDateTime.parse(chat.initiatorLastSeen, dateTimeFormat)
                            } else {
                                userID = chat.initiatorUserID
                                lastSeen =
                                    LocalDateTime.parse(chat.receiverLastSeen, dateTimeFormat)
                            }

                            val user = userViewModel.getUserByID(userID)
                            userList.add(user!!)

                            val chatLineList = chatLineViewModel.getChatLine(chat.chatID).toMutableList()
                            chatLineList.sortByDescending { it.dateTime }

                            var counter = 0
                            for (eachChatLine in chatLineList) {
                                val dateTime = LocalDateTime.parse(
                                    eachChatLine.dateTime,
                                    dateTimeFormat
                                )

                                if (dateTime > lastSeen) {
                                    counter++
                                } else {
                                    unseenMsgList.add(counter)
                                    break
                                }
                            }
                        }

                        adapter.setUserList(userList)
                        adapter.setLastChatList(lastChatList, unseenMsgList)
                        recyclerView.adapter = adapter

                        // can add a "No such friend" if no record
                    }
                })

                chatLineViewModel.fetchLastChatList(chatIDList)
            }
        }
    }
}