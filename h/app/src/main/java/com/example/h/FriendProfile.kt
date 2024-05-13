package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class FriendProfile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friend_profile, container, false)

        val btnA : Button = view.findViewById(R.id.btnAddFriendFriendProfile)

        btnA.setOnClickListener {

            val fragment = Home()

            val transaction = activity?.supportFragmentManager?.beginTransaction()

            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()

        }

        return view
    }
}