package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.h.data.ChatLine
import com.example.h.data.GroupChatLine
import com.example.h.dataAdapter.GroupChatAdapter
import com.example.h.dataAdapter.InnerChatAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.GroupChatLineViewModel
import com.example.h.viewModel.GroupViewModel
import com.example.h.viewModel.UserViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GroupChat : Fragment() {

    private var groupChatLineList = mutableListOf<GroupChatLine>()

    private lateinit var currentUserID : String
    private lateinit var imgProfile : ImageView
    private lateinit var tvName : TextView

    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: GroupChatAdapter

    private val storageRef : StorageReference = FirebaseStorage.getInstance().getReference()

    private lateinit var groupChatLineViewModel : GroupChatLineViewModel

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_group_chat, container, false)

        (activity as MainActivity).setToolbar()

        if (arguments?.getString("groupID").isNullOrEmpty()) {

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = Groups()

            val bundle = Bundle()
            bundle.putString("NoSuchGroup", "No such group")
            fragment.arguments = bundle

            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()

        }

        val groupID = arguments?.getString("groupID")!!

        currentUserID = SaveSharedPreference.getUserID(requireContext())

        groupChatLineViewModel = ViewModelProvider(this).get(GroupChatLineViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerViewChatGroupChat)
        adapter = GroupChatAdapter(currentUserID)
        fetchData(groupID)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val btnBack : ImageView = view.findViewById(R.id.btnBackGroupChat)
        val txtChat : EditText = view.findViewById(R.id.txtChatGroupChat)
        val btnSend : ImageView = view.findViewById(R.id.btnSendGroupChat)
        imgProfile = view.findViewById(R.id.imgGroupGroupChat)
        tvName = view.findViewById(R.id.tvNameGroupChat)

        btnBack.setOnClickListener {
            // chatViewModel.updateLastSeen(chatID, currentUserID)
            activity?.supportFragmentManager?.popBackStack()
        }

        btnSend.setOnClickListener {
            val inputText = txtChat.text.toString()

            if (inputText.trim().isNotEmpty()) {
                val currentTime = LocalDateTime.now().format(dateTimeFormat)

                val groupChatLine = GroupChatLine("GC103", currentUserID, currentTime, groupID, inputText)
                groupChatLineViewModel.addGroupChatLine(groupChatLine)

                txtChat.text.clear()
            }
        }

        return view
    }

    private fun fetchData(groupID : String) {
        val groupViewModel: GroupViewModel = ViewModelProvider(this).get(GroupViewModel::class.java)

        lifecycleScope.launch {
            groupChatLineViewModel.fetchGroupChatLine(groupID)

            groupChatLineViewModel.groupChatLineList.observe(viewLifecycleOwner, Observer { groupChatline ->
                groupChatLineList.clear()

                groupChatLineList.addAll(groupChatline)
                groupChatLineList.sortBy { it.dateTime }


                adapter.setGroupChatLineList(groupChatLineList)
            })

            val group = groupViewModel.getGroup(groupID)!!

            val ref = storageRef.child("imageProfile").child(group.groupID + ".png")

            ref.downloadUrl
                .addOnCompleteListener {
                    Glide.with(imgProfile).load(it.result.toString()).into(imgProfile)
                }

            tvName.text = group.groupName
        }
    }
}