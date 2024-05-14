package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.data.User
import com.example.h.data.Profile
import com.example.h.dataAdapter.FriendAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserViewModel
import kotlinx.coroutines.launch

class SearchFriend : Fragment() {

    private lateinit var recyclerView : RecyclerView
    private val adapter = FriendAdapter(FriendAdapter.Mode.ADD)

    private lateinit var userViewModel : UserViewModel
    private lateinit var profileViewModel : ProfileViewModel

    private lateinit var tvSuggest : TextView

    private var userList : ArrayList<User> = arrayListOf()
    private val profileList : ArrayList<Profile> = arrayListOf()

    private val userID = SaveSharedPreference.getUserID(requireContext())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_friend, container, false)

        tvSuggest = view.findViewById(R.id.tvSuggestFriendsSearchFriend)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerViewFriendSearchFriend)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val btnAdd : AppCompatButton = view.findViewById(R.id.btnSearchSearchFriend)
        val txtSearch : EditText = view.findViewById(R.id.txtSearchSearchFriend)

        btnAdd.setOnClickListener {
            val inputText = txtSearch.text.toString()
            searchUser(inputText)
        }

        return view
    }

    private fun searchUser(inputText : String) {

        lifecycleScope.launch {
            userList = ArrayList(userViewModel.searchUser(inputText))

            for (user in userList) {
                val profile = profileViewModel.getProfile(user.userID)
                profileList.add(profile!!)
            }

            adapter.setUserList(userList, profileList)
            recyclerView.adapter = adapter

            tvSuggest.text = "Search Result"
        }
    }

    private fun setupDefault() {
        lifecycleScope.launch {
            val currentUserProfile = profileViewModel.getProfile(userID)
            val userIDList = profileViewModel.getUserListByCourse(currentUserProfile!!.userCourse)

            for (otherUserID in userIDList) {
                val user = userViewModel.getUserByID(otherUserID)
                userList.add(user!!)
                val profile = profileViewModel.getProfile(otherUserID)
                profileList.add(profile!!)
            }

            adapter.setUserList(userList, profileList)
            recyclerView.adapter = adapter
        }
    }
}