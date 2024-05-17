package com.example.h

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.h.data.Chat
import com.example.h.data.ChatLine
import com.example.h.data.Friend
import com.example.h.data.Profile
import com.example.h.data.User
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.ChatLineViewModel
import com.example.h.viewModel.ChatViewModel
import com.example.h.viewModel.FriendViewModel
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar : ProgressBar
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navigationView : NavigationView
    private lateinit var actionBarDrawerToggle : ActionBarDrawerToggle
    private val manager = supportFragmentManager

    private lateinit var userViewModel : UserViewModel
    private lateinit var profileViewModel : ProfileViewModel
    private lateinit var friendViewModel : FriendViewModel
    private lateinit var chatViewModel : ChatViewModel
    private lateinit var chatLineViewModel : ChatLineViewModel

    private lateinit var toolbarContainer : FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = findViewById(R.id.progressBar)
        drawerLayout = findViewById(R.id.main)
        navigationView = findViewById(R.id.navigationView)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayShowHomeEnabled(true)

        navigationView.setNavigationItemSelectedListener {
                menuItem ->
            when(menuItem.itemId) {
                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    val transaction = manager.beginTransaction()
                    val fragment = Home()
                    transaction.replace(R.id.fragmentContainerView, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()

                    true
                }
                R.id.nav_settings -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    val transaction = manager.beginTransaction()
                    val fragment = Settings()
                    transaction.replace(R.id.fragmentContainerView, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()

                    true
                }
                R.id.nav_chat -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    val transaction = manager.beginTransaction()
                    val fragment = OuterChat()
                    transaction.replace(R.id.fragmentContainerView, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()

                    true
                }
                R.id.nav_group -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    val transaction = manager.beginTransaction()
                    val fragment = Groups()
                    transaction.replace(R.id.fragmentContainerView, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()

                    true
                }
                R.id.nav_profile -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    val transaction = manager.beginTransaction()
                    val fragment = com.example.h.Profile()
                    transaction.replace(R.id.fragmentContainerView, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()

                    true
                }
                else -> {
                    false
                }
            }
        }

        val footerMenu : View = findViewById(R.id.footerMenu)
        footerMenu.setOnClickListener {
            SaveSharedPreference.setUserID(this, "")

            val transaction = manager.beginTransaction()
            val fragment = SignIn()
            transaction.replace(R.id.fragmentContainerView, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        toolbarContainer = findViewById(R.id.toolbarContainer)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        friendViewModel = ViewModelProvider(this).get(FriendViewModel::class.java)
        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        chatLineViewModel = ViewModelProvider(this).get(ChatLineViewModel::class.java)

        // initForTesting()
    }

    fun initForTesting() {

        val userList : List<User> = listOf(
            User("U100", "Ali", "Ali@gmail.com", "0123456789", "01-01-2003", "AliPsw", "What is your favourite movie?"),
            User("U101", "Bali", "Bali@gmail.com", "0198765432", "01-02-2003", "BaliPsw", "What is your favourite pet?"),
            User("U102", "Cali", "Cali@gmail.com", "0135792468", "01-03-2003", "CaliPsw", "What is your favourite month?"),
            User("U103", "Dali", "Dali@gmail.com", "0124683579", "01-04-2003", "DaliPsw", "What is your favourite subject?"),
            User("U104", "Eali", "Eali@gmail.com", "0192348765", "01-05-2003", "EaliPsw", "What is your favourite fruit?"),
            User("U105", "Fali", "Fali@gmail.com", "0127893456", "01-06-2003", "FaliPsw", "What is your favourite food?"),
        )

        val profileList : List<Profile> = listOf(
            Profile("U100", "Business, TARUMT", "Hi I am Ali", "", "English"),
            Profile("U101", "Information Technology, TARUMT", "Hi I am Bali", "", "Chinese"),
            Profile("U102", "Computer Science, TARUMT", "Hi I am Cali", "", "English"),
            Profile("U103", "Accounting, TARUMT", "Hi I am Dali", "", "Chinese"),
            Profile("U104", "Business, TARUMT", "Hi I am Eali", "", "Chinese"),
            Profile("U105", "Information Technology, TARUMT", "Hi I am Fali", "", "English"),
        )

        val friendList : List<Friend> = listOf(
            Friend("F100", "U100", "U101", "Friend"),
            Friend("F101", "U102", "U100", "Friend"),
            Friend("F102", "U103", "U100", "Friend"),
            Friend("F103", "U100", "U104", "Friend"),
            Friend("F104", "U105", "U101", "Friend"),
            Friend("F105", "U101", "U102", "Friend"),
        )

        val chatList : List<Chat> = listOf(
            Chat("C100", "U100", "U101", "2024-05-17 16:00:00", "2024-05-17 16:00:00")
        )

        val chatLineList : List<ChatLine> = listOf(
            ChatLine("CL100", "U100", "2024-05-17 15:58:00", "C100", "Hello I am Ali"),
            ChatLine("CL101", "U101", "2024-05-17 15:58:20", "C100", "Hello Ali"),
        )


        lifecycleScope.launch {
            userList.forEach { user ->
                try {
                    userViewModel.addUser(user)
                    // Delay for a short period to ensure sequential addition
                    delay(1000)
                } catch (e: Exception) {
                    // Handle exceptions if needed
                    Toast.makeText(this@MainActivity, "Error adding user", Toast.LENGTH_LONG).show()
                }
            }

            profileList.forEach { profile ->
                try {
                    profileViewModel.addProfile(profile)
                    // Delay for a short period to ensure sequential addition
                    delay(1000)
                } catch (e: Exception) {
                    // Handle exceptions if needed
                    Toast.makeText(this@MainActivity, "Error adding profile", Toast.LENGTH_LONG).show()
                }
            }

            friendList.forEach { friend ->
                try {
                    friendViewModel.addFriend(friend)
                    // Delay for a short period to ensure sequential addition
                    delay(1000)
                } catch (e: Exception) {
                    // Handle exceptions if needed
                    Toast.makeText(this@MainActivity, "Error adding friend", Toast.LENGTH_LONG).show()
                }
            }

            chatList.forEach { chat ->
                chatViewModel.addChat(chat)
                try {

                    // Delay for a short period to ensure sequential addition
                    delay(1000)
                } catch (e: Exception) {
                    // Handle exceptions if needed
                    Toast.makeText(this@MainActivity, "Error adding chat", Toast.LENGTH_LONG).show()
                }
            }

            chatLineList.forEach { chatLine ->
                chatLineViewModel.addChatLine(chatLine)
                try {

                    // Delay for a short period to ensure sequential addition
                    delay(1000)
                } catch (e: Exception) {
                    // Handle exceptions if needed
                    Toast.makeText(this@MainActivity, "Error adding chatLine", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    fun setToolbar(toolbarLayoutResId : Int, bgColorResId : Int = R.color.background) {
        toolbarContainer.visibility = View.VISIBLE

        toolbarContainer.removeAllViews()
        val toolbar : View = layoutInflater.inflate(toolbarLayoutResId, toolbarContainer, false)

        val btnSearchToolbarWithProfile = toolbar.findViewById<ImageView>(R.id.btnSearchToolbarWithProfile)

        btnSearchToolbarWithProfile.setOnClickListener {
            val fragment = SearchPost()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerView, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        toolbarContainer.addView(toolbar)
        toolbarContainer.setBackgroundColor(this.getColor(bgColorResId))

        setSupportActionBar(toolbar as Toolbar)

        supportActionBar?.title = ""

        toolbar.setNavigationOnClickListener {
            openDrawer()
        }



    }

    fun setToolbar() {
        toolbarContainer.removeAllViews()
        toolbarContainer.visibility = View.GONE
    }
}