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
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.example.h.dao.ProfileDAO
import com.example.h.data.Profile
import com.example.h.saveSharedPreference.SaveSharedPreference
import com.example.h.viewModel.ProfileViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch


class Profile : Fragment() {

    private lateinit var btnSettingProfile: ImageView
    private lateinit var changeImage: ImageView
    private lateinit var cardViewProfile: CardView
    private lateinit var btnEditProfile: Button
    private lateinit var userID: String
    private lateinit var profileDao: ProfileDAO
    private lateinit var storageRef: StorageReference
    private lateinit var profileViewModel: ProfileViewModel

    private val imagePickRequestCode = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        btnSettingProfile = view.findViewById(R.id.imgSettingsProfile)
        changeImage = view.findViewById(R.id.changeImage)
        cardViewProfile = view.findViewById(R.id.cardViewProfile)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        userID = SaveSharedPreference.getUserID(requireContext())
        profileDao = ProfileDAO()
        storageRef = FirebaseStorage.getInstance().reference.child("profile_images")

        loadProfilePicture()

        btnSettingProfile.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val fragment = Settings()
            transaction?.replace(R.id.fragmentContainerView, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        changeImage.setOnClickListener {
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

    private fun loadProfilePicture() {
        val userID = SaveSharedPreference.getUserID(requireContext())
        lifecycleScope.launch {
            val profile = profileViewModel.getProfile(userID)
            profile?.let {
                val imageUrl = it.userImage
                // Load image using Glide or any other image loading library
                Glide.with(requireContext())
                    .load(imageUrl)
                    .into(changeImage)
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
        val imageRef = storageRef.child("$userID.jpg")
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val newImageUrl = downloadUri.toString()
                    // Update the user's profile picture URL in Firebase Database
                    profileDao.updateProfilePicture(userID, newImageUrl)
                    // Load the updated profile picture
                    loadProfilePicture()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
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