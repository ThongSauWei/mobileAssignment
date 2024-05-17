package com.example.h

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.example.h.dao.ProfileDAO
import com.example.h.data.Post
import com.example.h.data.Profile
import com.example.h.dataAdapter.PostAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.FriendViewModel
import com.example.h.viewModel.PostViewModel
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserGroupViewModel
import com.example.h.viewModel.UserViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch


class Profile : Fragment() {

    private lateinit var friendViewModel : FriendViewModel
    private lateinit var userViewModel : UserViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var storageRef: StorageReference
    private lateinit var profileDao: ProfileDAO
    private lateinit var postList : List<Post>
    private lateinit var recyclerView : RecyclerView

    private lateinit var btnSettingProfile: ImageView
    private lateinit var changeImageClick: ImageView
    private lateinit var cardViewProfile: CardView
    private lateinit var btnEditProfile: Button
    private lateinit var currentUserID: String
    private lateinit var changeImage: ImageView
    private lateinit var scCreatePost: Button
    private lateinit var nameProfile: TextView
    private lateinit var DOBProfile: TextView
    private lateinit var courseProfile: TextView
    private lateinit var userBIOProfile: TextView
    private lateinit var ttlGroupProfile: TextView
    private lateinit var ttlFriendProfile: TextView
    private lateinit var languageProfile: TextView

    private val imagePickRequestCode = 1000



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        (activity as MainActivity).setToolbar(R.layout.toolbar_with_profile, R.color.profile_bg)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        loadProfilePicture()
        nameProfile = view.findViewById(R.id.tvNameProfile)
        DOBProfile = view.findViewById(R.id.tvDOBProfile)
        courseProfile = view.findViewById(R.id.tvCoursesProfile)
        userBIOProfile = view.findViewById(R.id.tvBioProfile)
        ttlGroupProfile = view.findViewById(R.id.tvGroupsProfile)
        ttlFriendProfile = view.findViewById(R.id.tvFriendsProfile)
        languageProfile = view.findViewById(R.id.tvLanguages)

        btnSettingProfile = view.findViewById(R.id.imgSettingsProfile)
        changeImageClick = view.findViewById(R.id.changeImage)
        changeImage = view.findViewById(R.id.imgProfileProfile)
        cardViewProfile = view.findViewById(R.id.cardViewProfile)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        scCreatePost = view.findViewById(R.id.btnCreatePostProfile)
        currentUserID = SaveSharedPreference.getUserID(requireContext())
        profileDao = ProfileDAO()
        storageRef = FirebaseStorage.getInstance().reference.child("profile_images")

        friendViewModel = ViewModelProvider(this).get(FriendViewModel::class.java)

        loadProfilePicture()

        recyclerView = view.findViewById(R.id.recyclerViewProfilePost)
        setupProfileInfo(currentUserID)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        scCreatePost.setOnClickListener{
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = CreatePost()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        btnSettingProfile.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = Settings()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        changeImageClick.setOnClickListener {
            pickImageFromGallery()
        }

        btnEditProfile.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = EditProfile()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        // Display user's posts
        //displayUserPosts()

        return view
    }

    private fun setupProfileInfo(userID : String){
        val userViewModel : UserViewModel =
            ViewModelProvider(this).get(UserViewModel::class.java)
        val profileViewModel : ProfileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val userGroupViewModel : UserGroupViewModel =
            ViewModelProvider(this).get(UserGroupViewModel::class.java)
        val postViewModel : PostViewModel =
            ViewModelProvider(this).get(PostViewModel::class.java)

        lifecycleScope.launch {
            val user = userViewModel.getUserByID(userID)!!
            val profile = profileViewModel.getProfile(userID)
            val totalGroup = userGroupViewModel.getUserGroupByUser(userID).size
            val totalFriend = friendViewModel.getFriendList(userID).size
            postList = postViewModel.getPostByUser(userID)

            nameProfile.text = user.username
            DOBProfile.text = user.userDOB
            if (profile != null) {
                courseProfile.text = profile.userCourse
            }
            if (profile != null) {
                userBIOProfile.text = profile.userBio
            }
            if (profile != null) {
                languageProfile.text = profile.userChosenLanguage
            }
            ttlGroupProfile.text = totalGroup.toString()
            ttlFriendProfile.text = totalFriend.toString()

            if (postList.size > 0) {
                val adapter = PostAdapter(postList)
                adapter.setViewModel(userViewModel)
                recyclerView.adapter = adapter
            }

        }
    }

    private fun loadProfilePicture() {
        val userID = SaveSharedPreference.getUserID(requireContext())
        lifecycleScope.launch {
            val profile = profileViewModel.getProfile(userID)
            profile?.let {
                val imageUrl = it.userImage
                // Load image using Picasso
                val imageView = view?.findViewById<ImageView>(R.id.imgProfileProfile)
                if (imageUrl.isNotEmpty()) {
                    Picasso.get().load(imageUrl).into(imageView)
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, imagePickRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagePickRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            data.data?.let { imageUri ->
                updateProfilePicture(imageUri)
            }
        }
    }

    private fun updateProfilePicture(imageUri: Uri) {
        // Upload the image to Firebase Storage
        val imageRef = storageRef.child("$currentUserID.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val newImageUrl = downloadUri.toString()
                    // Update the user's profile picture URL in Firebase Database
                    profileDao.updateProfilePicture(currentUserID, newImageUrl)
                    // Load the updated profile picture
                    loadProfilePicture()

                    navigateToProfile()

                    Toast.makeText(requireContext(), "Image change successful!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToProfile() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val fragment = com.example.h.Profile()
        transaction?.replace(R.id.fragmentContainerView, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    /*private fun displayUserPosts() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Posts").child(userID) // Assuming Posts are stored under each user's ID

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val container = view?.findViewById<LinearLayout>(R.id.containerForPosts)
                dataSnapshot.children.forEach { postSnapshot ->
                    val post = postSnapshot.getValue(String::class.java)
                    if (post != null) {
                        val cardView = layoutInflater.inflate(R.layout.cardview_post, null) as CardView
                        val textViewPost = cardView.findViewById<TextView>(R.id.textViewPost)
                        textViewPost.text = post
                        container?.addView(cardView)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("ProfileFragment", "Failed to read value.", databaseError.toException())
            }
        })*/

}