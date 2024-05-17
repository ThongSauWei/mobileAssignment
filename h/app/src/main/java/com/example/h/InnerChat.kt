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
import com.example.h.dataAdapter.InnerChatAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.ChatLineViewModel
import com.example.h.viewModel.ChatViewModel
import com.example.h.viewModel.UserViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InnerChat : Fragment() {

    private var chatLineList = mutableListOf<ChatLine>()

    private lateinit var currentUserID : String
    private lateinit var friendUserID : String
    private lateinit var imgProfile : ImageView
    private lateinit var tvName : TextView

    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: InnerChatAdapter

    private val storageRef : StorageReference = FirebaseStorage.getInstance().getReference()

    private lateinit var chatLineViewModel : ChatLineViewModel

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_inner_chat, container, false)

        (activity as MainActivity).setToolbar()

        if (arguments?.getString("chatID").isNullOrEmpty() || arguments?.getString("friendUserID").isNullOrEmpty()) {

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = OuterChat()

            val bundle = Bundle()
            bundle.putString("NoSuchChat", "No such chat")
            fragment.arguments = bundle

            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()

        }

        val chatID = arguments?.getString("chatID")!!
        friendUserID = arguments?.getString("friendUserID")!!

        currentUserID = SaveSharedPreference.getUserID(requireContext())

        val chatViewModel : ChatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        chatLineViewModel = ViewModelProvider(this).get(ChatLineViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerViewChatInnerChat)
        adapter = InnerChatAdapter(currentUserID)
        fetchData(chatID)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val btnBack : ImageView = view.findViewById(R.id.btnBackInnerChat)
        val txtChat : EditText = view.findViewById(R.id.txtChatInnerChat)
        val btnSend : ImageView = view.findViewById(R.id.btnSendInnerChat)
        imgProfile = view.findViewById(R.id.imgProfileInnerChat)
        tvName = view.findViewById(R.id.tvNameInnerChat)

        btnBack.setOnClickListener {
            chatViewModel.updateLastSeen(chatID, currentUserID)
            activity?.supportFragmentManager?.popBackStack()
        }

        imgProfile.setOnClickListener {

            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = FriendProfile()

            val bundle = Bundle()
            bundle.putString("friendUserID", friendUserID)
            fragment.arguments = bundle

            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        btnSend.setOnClickListener {
            val inputText = txtChat.text.toString()

            if (inputText.trim().isNotEmpty()) {
                val currentTime = LocalDateTime.now().format(dateTimeFormat)

                val chatLine = ChatLine("CL103", currentUserID, currentTime, chatID, inputText)
                chatLineViewModel.addChatLine(chatLine)

                txtChat.text.clear()
            }
        }

        return view
    }

    private fun fetchData(chatID : String) {
        val userViewModel : UserViewModel =
            ViewModelProvider(this).get(UserViewModel::class.java)

        chatLineViewModel.fetchChatLine(chatID)

        chatLineViewModel.chatLineList.observe(viewLifecycleOwner, Observer { chatLines ->
            chatLineList.clear()

            chatLineList.addAll(chatLines)
            chatLineList.sortBy { it.dateTime }


            adapter.setChatLineList(chatLineList)
        })

        lifecycleScope.launch {
            val ref = storageRef.child("imageProfile").child(friendUserID + ".png")

            ref.downloadUrl
                .addOnCompleteListener {
                    Glide.with(imgProfile).load(it.result.toString()).into(imgProfile)
                }

            val user = userViewModel.getUserByID(friendUserID)

            tvName.text = user!!.username
        }
    }
}