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
import com.example.h.data.Chat
import com.example.h.data.ChatLine
import com.example.h.data.Post
import com.example.h.data.Profile
import com.example.h.data.User
import com.example.h.dataAdapter.FriendAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.ChatLineViewModel
import com.example.h.viewModel.ChatViewModel
import com.example.h.viewModel.FriendViewModel
import com.example.h.viewModel.PostViewModel
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserViewModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class InviteFriend : Fragment() {
    private lateinit var recyclerView : RecyclerView

    private var userList : ArrayList<User> = arrayListOf()
    private val profileList : ArrayList<Profile> = arrayListOf()

    private lateinit var userViewModel : UserViewModel
    private lateinit var profileViewModel : ProfileViewModel
    private lateinit var postViewModel: PostViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatLineViewModel: ChatLineViewModel

    private val clickedFriendIDs = mutableListOf<String>()

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

        //viewmodel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        chatLineViewModel = ViewModelProvider(this).get(ChatLineViewModel::class.java)


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

        lifecycleScope.launch {
            val lastPostID = postViewModel.getLastPostID(getCurrentUserID())
            Log.d("InviteFriend", "Last post ID: $lastPostID")

            btnDoneInviteFriend.setOnClickListener {
                // Log the clicked friend IDs
                for (friendID in clickedFriendIDs) {
                    Log.d("InviteFriend", "Selected friendID done: $friendID")
                    // Fetch chat id
                    lifecycleScope.launch {
                        val chat = chatViewModel.getChat(getCurrentUserID(), friendID)
                        Log.d("InviteFriend", "chat: $chat")

                        //content is "Join $postid now"
                        if (chat == null) {
                            //create chat first then use the created chatid create a chatline
                            val chatObject = Chat(
                                chatID = "",
                                initiatorUserID = getCurrentUserID(),
                                receiverUserID = friendID,
                                initiatorLastSeen = getCurrentDateTime(),
                                receiverLastSeen = getCurrentDateTime()
                            )

                            chatViewModel.addChat(chatObject)

                            val fragment = Home()
                            val transaction = activity?.supportFragmentManager?.beginTransaction()
                            transaction?.replace(R.id.fragmentContainerView, fragment)
                            transaction?.addToBackStack(null)
                            transaction?.commit()

                            val chatIDGet = chatViewModel.getChat(getCurrentUserID(), friendID)
                            Log.d("chatIDGetIf", chatIDGet.toString())


                            val chatLineObject = chatIDGet?.let { it1 ->
                                ChatLine(
                                    chatLineID = "",
                                    senderID = getCurrentUserID(),
                                    dateTime = getCurrentDateTime(),
                                    chatID = it1.chatID,
                                    content = "Join Now! $lastPostID"
                                )
                            }

                            if (chatLineObject != null) {
                                chatLineViewModel.addChatLine(chatLineObject)
                                Log.d("chatLineObjectIf", chatLineObject.toString())
                            }

                        } else {
                            // if chatid not null, direct use that chatid create a chatline
                            val chatLineObject = ChatLine(
                                chatLineID = "",
                                senderID = getCurrentUserID(),
                                dateTime = getCurrentDateTime(),
                                chatID = chat.chatID,
                                content = "Join Now! $$lastPostID$"
                            )

                            chatLineViewModel.addChatLine(chatLineObject)
                            Log.d("chatLineObjectElse", chatLineObject.toString())

                            val fragment = Home()
                            val transaction = activity?.supportFragmentManager?.beginTransaction()
                            transaction?.replace(R.id.fragmentContainerView, fragment)
                            transaction?.addToBackStack(null)
                            transaction?.commit()

                        }
                    }
                }


            }
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
                adapter.setOnItemClickListener { user, friendID, imageView ->
                    imageView.setImageResource(R.drawable.baseline_check_24)
                    if (!clickedFriendIDs.contains(friendID)) {
                        clickedFriendIDs.add(friendID)
                    }
                    Log.d("InviteFriend", "Clicked friendID: $friendID")
                }
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
                    adapter.setOnItemClickListener { user, friendID, imageView ->
                        imageView.setImageResource(R.drawable.baseline_check_24)
                        if (!clickedFriendIDs.contains(friendID)) {
                            clickedFriendIDs.add(friendID)
                        }
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

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
