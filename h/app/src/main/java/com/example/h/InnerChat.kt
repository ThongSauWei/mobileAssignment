package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.data.ChatLine
import com.example.h.dataAdapter.InnerChatAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.ChatLineViewModel
import kotlinx.coroutines.launch

class InnerChat : Fragment() {

    private var chatLineList = mutableListOf<ChatLine>()

    private lateinit var currentUserID : String

    private lateinit var recyclerView : RecyclerView

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
        val friendUserID = arguments?.getString("friendUserID")!!

        currentUserID = SaveSharedPreference.getUserID(requireContext())

        recyclerView = view.findViewById(R.id.recyclerViewChatInnerChat)
        fetchData(chatID)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val btnBack : ImageView = view.findViewById(R.id.btnBackInnerChat)
        val imgProfile : ImageView = view.findViewById(R.id.imgProfileInnerChat)

        btnBack.setOnClickListener {
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

        return view
    }

    private fun fetchData(chatID : String) {
        val chatLineViewModel : ChatLineViewModel =
            ViewModelProvider(this).get(ChatLineViewModel::class.java)

        chatLineViewModel.chatLineList.observe(viewLifecycleOwner, Observer { chatLines ->
            chatLineList.clear()

            chatLineList.addAll(chatLines)
            chatLineList.sortBy { it.dateTime }

            val adapter = InnerChatAdapter(currentUserID)
            adapter.setChatLineList(chatLineList)
            recyclerView.adapter = adapter
        })

        chatLineViewModel.getChatLine(chatID)
    }
}