package com.example.h

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
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

    private var userList : ArrayList<User> = arrayListOf()
    private val profileList : ArrayList<Profile> = arrayListOf()

    private lateinit var userViewModel : UserViewModel
    private lateinit var profileViewModel : ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_invite_friend, container, false)

        (activity as MainActivity).setToolbar(R.layout.toolbar_with_profile)

        recyclerView = view.findViewById(R.id.recyclerViewFriendInviteFriend)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)


        (activity as MainActivity).showProgressBar()

        fetchData().observe(viewLifecycleOwner, Observer { isDataFetched ->
            if (isDataFetched) {
                (activity as MainActivity).hideProgressBar()
            }
        })

        val btnSearchInvite = view.findViewById<ImageView>(R.id.btnSearchInvite)
        val btnDoneInviteFriend = view.findViewById<AppCompatButton>(R.id.btnDoneInviteFriend)
        val txtSearchInviteFriend: EditText = view.findViewById(R.id.txtSearchInviteFriend)

        btnSearchInvite.setOnClickListener {
            val inputText = txtSearchInviteFriend.text.toString()
            searchUser(inputText)
        }

        btnDoneInviteFriend.setOnClickListener {
            val fragment = CreatePost()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }

    private fun searchUser(inputText : String) {
        if (inputText.isEmpty()) {
            fetchData()
        } else {
            userList.clear()
            profileList.clear()

            lifecycleScope.launch {
                userList = ArrayList(userViewModel.searchUser(inputText))
                userList.removeIf { it.userID == getCurrentUserID() }

                for (user in userList) {
                    val profile = profileViewModel.getProfile(user.userID)
                    profileList.add(profile!!)
                }

                val adapter = FriendAdapter(FriendAdapter.Mode.INVITE)
                adapter.setUserList(userList)
                adapter.setProfileList(profileList)
                recyclerView.adapter = adapter
            }
        }
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
                    adapter.setUserList(userList)
                    adapter.setProfileList(profileList)
                    adapter.setFriendList(friendList)
                    //change to check icon
                    adapter.setOnItemClickListener { user, friendID, imageView ->
                        imageView.setImageResource(R.drawable.baseline_check_24)
                        Log.d("InviteFriend", "Clicked friendID: $friendID")
                    }
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
