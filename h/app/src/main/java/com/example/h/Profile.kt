package com.example.h

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.example.h.dao.ProfileDAO
import com.example.h.data.Post
import com.example.h.dataAdapter.PostAdapter
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.FriendViewModel
import com.example.h.viewModel.PostViewModel
import com.example.h.viewModel.ProfileViewModel
import com.example.h.viewModel.UserGroupViewModel
import com.example.h.viewModel.UserViewModel
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
    private lateinit var lblGroupClick: TextView
    private lateinit var lblFriendClick: TextView

    private val imagePickRequestCode = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        (activity as MainActivity).setToolbar(R.layout.toolbar_with_profile, R.color.profile_bg)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        nameProfile = view.findViewById(R.id.tvNameProfile)
        DOBProfile = view.findViewById(R.id.tvDOBProfile)
        courseProfile = view.findViewById(R.id.tvCoursesProfile)
        userBIOProfile = view.findViewById(R.id.tvBioProfile)
        ttlGroupProfile = view.findViewById(R.id.tvGroupsProfile)
        ttlFriendProfile = view.findViewById(R.id.tvFriendsProfile)
        languageProfile = view.findViewById(R.id.tvLanguages)
        lblGroupClick = view.findViewById(R.id.lblGroupsProfile)
        lblFriendClick = view.findViewById(R.id.lblFriendsProfile)

        btnSettingProfile = view.findViewById(R.id.imgSettingsProfile)
        changeImageClick = view.findViewById(R.id.changeImage)
        changeImage = view.findViewById(R.id.imgProfileProfile)
        cardViewProfile = view.findViewById(R.id.cardViewProfile)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        scCreatePost = view.findViewById(R.id.btnCreatePostProfile)
        currentUserID = SaveSharedPreference.getUserID(requireContext())
        profileDao = ProfileDAO()
        storageRef = FirebaseStorage.getInstance().getReference()

        friendViewModel = ViewModelProvider(this).get(FriendViewModel::class.java)

        loadProfilePicture()

        recyclerView = view.findViewById(R.id.recyclerViewProfilePost)
        setupProfileInfo(currentUserID)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        scCreatePost.setOnClickListener {
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

        ttlFriendProfile.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = Friends()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        lblFriendClick.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = Friends()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        lblGroupClick.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = Groups()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        ttlGroupProfile.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = Groups()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        btnEditProfile.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = EditProfile()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }

    private fun setupProfileInfo(userID: String) {
        val userViewModel: UserViewModel =
            ViewModelProvider(this).get(UserViewModel::class.java)
        val profileViewModel: ProfileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        val userGroupViewModel: UserGroupViewModel =
            ViewModelProvider(this).get(UserGroupViewModel::class.java)
        val postViewModel: PostViewModel =
            ViewModelProvider(this).get(PostViewModel::class.java)

        lifecycleScope.launch {
            val user = userViewModel.getUserByID(userID)!!
            val profile = profileViewModel.getProfile(userID)
            val totalGroup = userGroupViewModel.getUserGroupByUser(userID).size
            val totalFriend = friendViewModel.getFriendList(userID).size
            postList = postViewModel.getPostByUser(userID)

            nameProfile.text = user.username
            DOBProfile.text = user.userDOB
            courseProfile.text = profile?.userCourse ?: ""
            userBIOProfile.text = profile?.userBio ?: ""
            languageProfile.text = profile?.userChosenLanguage ?: ""
            ttlGroupProfile.text = totalGroup.toString()
            ttlFriendProfile.text = totalFriend.toString()

            if (postList.isNotEmpty()) {
                val adapter = PostAdapter(postList)
                adapter.setViewModel(userViewModel)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun loadProfilePicture() {
        val userID = SaveSharedPreference.getUserID(requireContext())
        val ref = storageRef.child("imageProfile").child("$userID.png")

        ref.downloadUrl.addOnCompleteListener {
            if (it.isSuccessful) {
                val imageUrl = it.result.toString()
                val imageView = view?.findViewById<ImageView>(R.id.imgProfileProfile)
                Picasso.get().load(imageUrl).into(imageView)
            } else {
                Toast.makeText(requireContext(), "Failed to load profile picture", Toast.LENGTH_SHORT).show()
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
        val imageRef = storageRef.child("imageProfile").child("$currentUserID.png")
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val newImageUrl = downloadUri.toString()
                    profileDao.updateProfilePicture(currentUserID, newImageUrl)
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
        val fragment = Profile()
        transaction?.replace(R.id.fragmentContainerView, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
}