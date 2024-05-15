package com.example.h

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.h.data.Friend
import com.example.h.data.Profile
import com.example.h.data.User
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.FriendViewModel
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserViewModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar : ProgressBar
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navigationView : NavigationView
    private lateinit var actionBarDrawerToggle : ActionBarDrawerToggle
    private val manager = supportFragmentManager

    private lateinit var userViewModel : UserViewModel
    private lateinit var profileViewModel : ProfileViewModel
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

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        friendViewModel = ViewModelProvider(this).get(FriendViewModel::class.java)

        initForTesting()
    }

    fun initForTesting() {

        val userList : List<User> = listOf(
            User("A100", "Ali", "Ali@gmail.com", "0123456789", "01-01-2003", "AliPsw", "What is your favourite movie?"),
            User("A101", "Bali", "Bali@gmail.com", "0198765432", "01-02-2003", "BaliPsw", "What is your favourite pet?"),
            User("A102", "Cali", "Cali@gmail.com", "0135792468", "01-03-2003", "CaliPsw", "What is your favourite month?"),
            User("A103", "Dali", "Dali@gmail.com", "0124683579", "01-04-2003", "DaliPsw", "What is your favourite subject?"),
            User("A104", "Eali", "Eali@gmail.com", "0192348765", "01-05-2003", "EaliPsw", "What is your favourite fruit?"),
            User("A105", "Fali", "Fali@gmail.com", "0127893456", "01-06-2003", "FaliPsw", "What is your favourite food?"),
        )

        val profileList : List<Profile> = listOf(
            Profile("A100", "Business, TARUMT", "Hi I am Ali", "", "English"),
            Profile("A101", "Information Technology, TARUMT", "Hi I am Bali", "", "Chinese"),
            Profile("A102", "Computer Science, TARUMT", "Hi I am Cali", "", "English"),
            Profile("A103", "Accounting, TARUMT", "Hi I am Dali", "", "Chinese"),
            Profile("A104", "Business, TARUMT", "Hi I am Eali", "", "Chinese"),
            Profile("A105", "Information Technology, TARUMT", "Hi I am Fali", "", "English"),
        )

        val friendList : List<Friend> = listOf(
            Friend("F100", "A100", "A101", "Friend"),
            Friend("F101", "A102", "A100", "Friend"),
            Friend("F102", "A103", "A100", "Friend"),
            Friend("F103", "A100", "A104", "Friend"),
            Friend("F104", "A105", "A101", "Friend"),
            Friend("F105", "A101", "A102", "Friend"),
        )

        for (user in userList) {
            userViewModel.addUser(user)
        }

        for (profile in profileList) {
            profileViewModel.addProfile(profile)
        }

        for (friend in friendList) {
            friendViewModel.addFriend(friend)
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
}