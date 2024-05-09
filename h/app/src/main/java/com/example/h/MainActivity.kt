package com.example.h

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.h.data.Friend
import com.example.h.data.User
import com.example.h.viewModel.FriendViewModel
import com.example.h.viewModel.UserViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var userViewModel : UserViewModel
    private lateinit var friendViewModel : FriendViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // initForTesting()
    }

    fun initForTesting() {

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        friendViewModel = ViewModelProvider(this).get(FriendViewModel::class.java)

        val userList : List<User> = listOf(
            User("A100", "Ali", "Ali@gmail.com", "0123456789", "01-01-2003", "AliPsw", "What is your favourite movie?"),
            User("A101", "Bali", "Bali@gmail.com", "0198765432", "01-02-2003", "BaliPsw", "What is your favourite pet?"),
            User("A102", "Cali", "Cali@gmail.com", "0135792468", "01-03-2003", "CaliPsw", "What is your favourite month?"),
            User("A103", "Dali", "Dali@gmail.com", "0124683579", "01-04-2003", "DaliPsw", "What is your favourite subject?"),
            User("A104", "Eali", "Eali@gmail.com", "0192348765", "01-05-2003", "EaliPsw", "What is your favourite fruit?"),
            User("A105", "Fali", "Fali@gmail.com", "0127893456", "01-06-2003", "FaliPsw", "What is your favourite food?"),
        )

        val friendList : List<Friend> = listOf(
            Friend("F100", "A100", "A101", "Friend"),
            Friend("F101", "A102", "A100", "Friend"),
            Friend("F102", "A103", "A100", "Friend"),
            Friend("F103", "A100", "A104", "Friend"),
            Friend("F104", "A105", "A100", "Friend"),
            Friend("F105", "A101", "A102", "Friend"),
        )

        for (user in userList) {
            userViewModel.addUser(user)
        }

        for (friend in friendList) {
            friendViewModel.addFriend(friend)
        }
    }
}