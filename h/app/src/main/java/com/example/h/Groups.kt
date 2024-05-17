package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.data.ChatLine
import com.example.h.data.Group
import com.example.h.data.GroupChatLine
import com.example.h.data.User
import com.example.h.data.UserGroup
import com.example.h.dataAdapter.GroupAdapter
import com.example.h.viewModel.ChatLineViewModel
import com.example.h.viewModel.ChatViewModel
import com.example.h.viewModel.GroupChatLineViewModel
import com.example.h.viewModel.GroupViewModel
import com.example.h.viewModel.UserGroupViewModel
import com.example.h.viewModel.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Groups : Fragment() {

    private lateinit var recyclerView : RecyclerView
    private val adapter = GroupAdapter()

    private lateinit var userViewModel : UserViewModel
    private lateinit var userGroupViewModel : UserGroupViewModel
    private lateinit var groupViewModel : GroupViewModel
    private lateinit var groupChatViewModel : GroupChatLineViewModel

    private var groupList : ArrayList<Group> = arrayListOf()
    private var lastChatList = mutableListOf<GroupChatLine>()
    private var unseenMsgList : ArrayList<Int> = arrayListOf()

    private lateinit var currentUserID : String

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        (activity as MainActivity).setToolbar(R.layout.toolbar_with_profile)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        groupViewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
        groupChatViewModel = ViewModelProvider(this).get(GroupChatLineViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerViewGroupGroups)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        adapter.setFragmentManager(parentFragmentManager)

        val btnSearch : AppCompatButton = view.findViewById(R.id.btnSearchGroups)
        val txtSearch : EditText = view.findViewById(R.id.txtSearchGroups)

        setupDefault()

        btnSearch.setOnClickListener {
            val inputText = txtSearch.text.toString()
            searchGroup(inputText)
        }

        return view
    }

    private fun setupDefault() {

        lifecycleScope.launch {
            val userGroupList = userGroupViewModel.getUserGroupByUser(currentUserID).toMutableList()

            userGroupList.forEach {
                val lastChat = groupChatViewModel.getLastGroupChat(it.groupID)
                lastChatList.add(lastChat!!)
            }

            lastChatList.sortByDescending { it.dateTime }

            lastChatList.forEach { chatLine ->
                val group = groupViewModel.getGroup(chatLine.groupID)
                groupList.add(group!!)
                val userGroup = userGroupList.filter { it.groupID == chatLine.groupID }.get(0)

                val lastSeen = LocalDateTime.parse(userGroup.lastSeen, dateTimeFormat)

                val groupChatLineList = groupChatViewModel.getGroupChatLine(chatLine.groupID).toMutableList()
                groupChatLineList.sortByDescending { it.dateTime }

                var counter = 0
                for (eachChatLine in groupChatLineList) {
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
            adapter.setGroupList(groupList)
            adapter.setLastChatList(lastChatList, unseenMsgList)
            recyclerView.adapter = adapter
        }
    }

    private fun searchGroup(txtSearch : String) {

    }
}