package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.dataAdapter.FriendAdapter
import com.example.h.viewModel.UserViewModel

class InviteFriend : Fragment() {

    private lateinit var userViewModel : UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_invite_friend, container, false)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerViewFriendInviteFriend)
        recyclerView.adapter = FriendAdapter(FriendAdapter.Mode.INVITE)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        return view
    }
}