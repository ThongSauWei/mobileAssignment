package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.dataAdapter.FriendAdapter

class InviteFriend : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_invite_friend, container, false)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerViewFriendInviteFriend)
        recyclerView.adapter = FriendAdapter(FriendAdapter.Mode.INVITE)
        recyclerView.layoutManager = LinearLayoutManager(activity?.application)
        recyclerView.setHasFixedSize(true)

        return view
    }
}