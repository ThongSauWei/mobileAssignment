package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.dataAdapter.FriendAdapter

class SearchFriend : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_friend, container, false)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerViewFriendSearchFriend)
        recyclerView.adapter = FriendAdapter(FriendAdapter.Mode.ADD)
        recyclerView.layoutManager = LinearLayoutManager(activity?.application)
        recyclerView.setHasFixedSize(true)

        return view
    }
}