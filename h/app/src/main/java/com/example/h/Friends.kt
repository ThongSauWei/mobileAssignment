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
import com.example.h.viewModel.FriendViewModel
import com.example.h.viewModel.UserViewModel

class Friends : Fragment() {

    private lateinit var friendViewModel : FriendViewModel
    private lateinit var userViewModel : UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friends, container, false)

        friendViewModel = ViewModelProvider(this).get(FriendViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerViewFriendFriends)
        recyclerView.adapter = FriendAdapter(userViewModel, FriendAdapter.Mode.DELETE)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        return view
    }
}