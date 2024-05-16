package com.example.h

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.dataAdapter.FriendAdapter
import com.example.h.viewModel.PostViewModel
import com.example.h.viewModel.UserViewModel

class InviteFriend : Fragment() {

    private lateinit var userViewModel : UserViewModel
    private lateinit var postViewModel: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_invite_friend, container, false)



        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerViewFriendInviteFriend)
        recyclerView.adapter = FriendAdapter(FriendAdapter.Mode.INVITE)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)


        // Observe postLiveData
        postViewModel.postLiveData.observe(viewLifecycleOwner) { post ->
            post?.let {
                // Use the post object
                Log.d("InviteFriend", "Received post: $post")
            }
        }

        arguments?.getString("success_message")?.let { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }

        return view
    }
}