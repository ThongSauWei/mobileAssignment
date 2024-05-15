package com.example.h

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.h.data.Profile
import com.example.h.data.User
import com.example.h.dataAdapter.FriendAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.FriendViewModel
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserViewModel
import kotlinx.coroutines.launch

class InviteFriend : Fragment() {
    private lateinit var recyclerView : RecyclerView

    private val userList : ArrayList<User> = arrayListOf()
    private val profileList : ArrayList<Profile> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_invite_friend, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewFriendInviteFriend)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        (activity as MainActivity).showProgressBar()

        fetchData().observe(viewLifecycleOwner, Observer { isDataFetched ->
            if (isDataFetched) {
                (activity as MainActivity).hideProgressBar()
            }
        })

        return view
    }

    private fun fetchData() : LiveData<Boolean> {
        val isDataFetched = MutableLiveData<Boolean>()

        val userViewModel : UserViewModel =
            ViewModelProvider(this).get(UserViewModel::class.java)
        val friendViewModel: FriendViewModel =
            ViewModelProvider(this).get(FriendViewModel::class.java)
        val profileViewModel: ProfileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        val userID = getCurrentUserID()

        lifecycleScope.launch {

            friendViewModel.friendList.observe(viewLifecycleOwner, Observer { friendList ->

                userList.clear()
                profileList.clear()

                lifecycleScope.launch {

                    for (friend in friendList) {
                        val friendID =
                            if (friend.requestUserID == userID) friend.receiveUserID else friend.requestUserID
                        val user = userViewModel.getUserByID(friendID)
                        userList.add(user!!)
                        val userProfile = profileViewModel.getProfile(user.userID)
                        profileList.add(userProfile!!)
                    }

                    val adapter = FriendAdapter(FriendAdapter.Mode.INVITE)
                    adapter.setUserList(userList, profileList)
                    adapter.setFriendList(friendList)
                    recyclerView.adapter = adapter

                    isDataFetched.value = true
                }
            })
        }

        return isDataFetched
    }

    private fun getCurrentUserID(): String {
        return SaveSharedPreference.getUserID(requireContext())
    }
}
